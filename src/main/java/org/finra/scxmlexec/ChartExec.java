package org.finra.scxmlexec;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.jsp.ELContext;
import org.apache.commons.scxml.env.jsp.ELEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

public class ChartExec {

    private static final Logger log = Logger.getLogger(ChartExec.class);

    /**
     * A comma separated list of variables to be passed to the OutputFormatter
     */
    private String outputVariables;

    /**
     * The input SCXML chart file
     */
    private String inputFileName = null;

    /**
     * The set of initial variables that the user wants to set
     */
    private String initialVariables = null;

    private static HashSet<String> varsOut = null;
    /**
     * The initial set of events to trigger before re-searching for a new
     * scenario
     */
    private String initialEvents = null;

    private static final ArrayList<String> initialEventsList = new ArrayList<String>();

    /**
     * Length of scenario
     */
    private int lengthOfScenario = 5;

    /**
     * Generate -ve scenarios
     */
    private boolean generateNegativeScenarios = false;

    /**
     * The state machine
     */
    private static SCXML stateMachine = null;

    /**
     * The state machine executor
     */
    private static SCXMLExecutor executor;

    /**
     * The state machine evaluator
     */
    private static ELEvaluator elEvaluator;

    /**
     * The state machine executor
     */
    private static Context context;

    /**
     * Initial variables map
     */
    private static final HashMap<String, String> initialVariablesMap = new HashMap<String, String>();

    private int maxEventReps = 1;

    private int maxScenarios = 10000;

    private static final StateMachineListener listener = new StateMachineListener();

    public String getOutputVariables() {
        return outputVariables;
    }

    public void setOutputVariables(String outputVariables) {
        this.outputVariables = outputVariables;
    }

    public String getInitialEvents() {
        return initialEvents;
    }

    public void setInitialEvents(String initialEvents) {
        this.initialEvents = initialEvents;
    }

    public String getInitialVariables() {
        return initialVariables;
    }

    public void setInitialVariables(String initialVariables) {
        this.initialVariables = initialVariables;
    }

    private OutputStream os = null;

    public OutputStream getOs() {
        return os;
    }

    public void setOs(OutputStream os) {
        this.os = os;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    private SequenceFile.Writer sequenceFileWriter = null;

    public SequenceFile.Writer getSequenceFileWriter() {
        return sequenceFileWriter;
    }

    public void setSequenceFileWriter(SequenceFile.Writer sequenceFileWriter) {
        this.sequenceFileWriter = sequenceFileWriter;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public boolean isGenerateNegativeScenarios() {
        return generateNegativeScenarios;
    }

    public void setGenerateNegativeScenarios(boolean generateNegativeScenarios) {
        this.generateNegativeScenarios = generateNegativeScenarios;
    }

    public int getLengthOfScenario() {
        return lengthOfScenario;
    }

    public void setLengthOfScenario(int lengthOfScenario) {
        this.lengthOfScenario = lengthOfScenario;
    }

    public int getMaxEventReps() {
        return maxEventReps;
    }

    public void setMaxEventReps(int maxEventReps) {
        this.maxEventReps = maxEventReps;
    }

    public int getMaxScenarios() {
        return maxScenarios;
    }

    public void setMaxScenarios(int maxScenarios) {
        this.maxScenarios = maxScenarios;
    }

    private boolean doSanityChecks() throws IOException {
        /*        if (outputVariables == null) {
         throw new IOException("Cannot continuw with outputVariables=null");
         }*/

        if (inputFileName == null) {
            throw new IOException("Error:, input file cannot be null");
        }

        if (!(new File(inputFileName)).exists()) {
            throw new IOException("Error:, input file does not exist");
        }

        // Parse the initial events
        if (initialEvents != null) {
            initialEventsList.addAll(Arrays.asList(StringUtils.split(initialEvents, ",")));
        }

        // Parse the initial variables
        if (initialVariables != null) {
            String[] vars = StringUtils.split(initialVariables, ",");
            for (String var : vars) {
                if (var.contains("=")) {
                    String[] assignment = var.split("=");
                    if (assignment[0] == null || assignment[1] == null || assignment[0].length() == 0
                            || assignment[1].length() == 0) {
                        throw new IOException("Error while processing initial variable assignment for: " + var);
                    }
                    initialVariablesMap.put(assignment[0], assignment[1]);
                } else {
                    throw new IOException("Error while processing initial variable assignment for: " + var);
                }
            }
        }

        return true;
    }

    /**
     * Reset the state machine, set the initial variables, and trigger the
     * initial events
     *
     * @throws ModelException
     */
    private void resetStateMachine() throws ModelException {
        // Go to the initial state
        executor.reset();

        // Set the initial variables values
        for (String var : varsOut) {
            context.set(var, "");
        }

        for (Map.Entry<String, String> entry : initialVariablesMap.entrySet()) {
            context.set(entry.getKey(), entry.getValue());
        }

        listener.reset();
        executor.go();
        fireEvents(initialEvents);
    }

    /**
     * Executes a list of given events. The format is
     * [beforeState]-event-[afterState] with the before and after states and
     * their separators optional
     *
     * @param commaSeparatedEvents
     * @throws ModelException
     */
    private void fireEvents(String commaSeparatedEvents) throws ModelException {
        if (commaSeparatedEvents == null) {
            return;
        }
        commaSeparatedEvents = commaSeparatedEvents.trim();
        if (commaSeparatedEvents == null || commaSeparatedEvents.length() == 0) {
            return;
        }

        String[] events = commaSeparatedEvents.split(",");
        for (String event : events) {
            log.debug("Firing event: " + event);
            String eventName = event;
            if (eventName.contains("-")) {
                String[] parts = event.split("-");
                eventName = parts[1];
            }
            log.debug("EventName:" + eventName);
            executor.triggerEvent(new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT));
        }
    }

    private void fireEvents(ArrayList<String> events) throws ModelException {
        if (events == null) {
            return;
        }

        for (String event : events) {
            log.debug("Firing event: " + event);
            String eventName = event;
            if (eventName.contains("-")) {
                String[] parts = event.split("-");
                eventName = parts[1];
            }
            log.debug("EventName:" + eventName);
            executor.triggerEvent(new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT));
        }
    }

    private HashSet<String> extractOutputVariables(String filePathName) throws IOException {
        log.info("Extracting variables from file: " + filePathName);
        List<String> linesRead = FileUtils.readLines(new File(filePathName));
        HashSet<String> outputVars = new HashSet<String>();
        for (String line : linesRead) {
            if (line.contains("var_out")) {
                int startIndex = line.indexOf("var_out");
                int lastIndex = startIndex;
                while (lastIndex < line.length() && (Character.isLetter(line.charAt(lastIndex))
                        || Character.isDigit(line.charAt(lastIndex))
                        || line.charAt(lastIndex) == '_'
                        || line.charAt(lastIndex) == '-')) {
                    lastIndex++;
                }
                if (lastIndex == line.length()) {
                    throw new IOException("Reached the end of the line while parsing variable name in line: '" + line + "'.");
                }
                String varName = line.substring(startIndex, lastIndex);
                log.info("Found variable: " + varName);
                outputVars.add(varName);
            }
        }

        return outputVars;
    }

    public void process() throws Exception {
        doSanityChecks();
        // Load the state machine
        String absolutePath = (new File(inputFileName)).getAbsolutePath();
        log.info("Processing file:" + absolutePath);
        varsOut = extractOutputVariables(absolutePath);
        stateMachine = SCXMLParser.parse(new URL("file://" + absolutePath), null);

        executor = new SCXMLExecutor();
        elEvaluator = new ELEvaluator();
        context = new ELContext();

        executor.setEvaluator(elEvaluator);
        executor.setStateMachine(stateMachine);
        executor.setRootContext(context);
        executor.addListener(stateMachine, listener);

        searchForScenariosDFS();
    }

    private void findEvents(ArrayList<String> positive, ArrayList<String> negative) throws ModelException,
            SCXMLExpressionException, IOException {
        positive.clear();
        negative.clear();
        TransitionTarget currentState = listener.getCurrentState();
        if (currentState == null) {
            throw new ModelException("Reached a null state");
        }
        List<Transition> transitions = currentState.getTransitionsList();
        for (Transition transition : transitions) {
            String condition = transition.getCond();
            List<TransitionTarget> targets = transition.getTargets();

            // In our case we should only have one target always
            if (targets == null) {
                throw new IOException("Found null targets for transition: " + transition.getEvent() + " in state: "
                        + currentState.getId());
            }

            if (targets.size() > 1 || targets.isEmpty()) {
                throw new IOException("Found incorrect number of targets:" + targets.size() + "for transition: "
                        + transition.getEvent() + " in state: " + currentState.getId());
            }

            String nextStateId = targets.get(0).getId();
            String transitionCode = currentState.getId() + "-" + transition.getEvent() + "-" + nextStateId;
            if (condition == null) {
                positive.add(transitionCode);
            } else {
                Boolean result = (Boolean) elEvaluator.eval(context, condition);
                if (result == null) {
                    throw new ModelException("Condition: " + condition + " evaluates to null");
                }

                if (result) {
                    positive.add(transitionCode);
                } else {
                    negative.add(transitionCode);
                }
            }
        }
    }

    private void printEvents(String type, ArrayList<String> events) {
        StringBuilder b = new StringBuilder();
        b.append(type);

        if (initialEvents != null) {
            b
                    .append(initialEvents)
                    .append(",");
        }

        boolean firstEvent = true;
        for (String event : events) {
            if (firstEvent) {
                firstEvent = false;
            } else {
                b.append(",");
            }
            b.append(event);
        }

        log.info(b);

    }

    /**
     * Defines a possible state that a state can be in. A possible state is a
     * combination of a state and values for variables.
     */
    static class PossibleState {

        /**
         * The name of the next state
         */
        String nextStateName;
        String transitionEvent;

        boolean varsInspected = false;

        /**
         * The variables that need to be set before jumping to that state
         */
        final HashMap<String, String> variablesAssignment = new HashMap<String, String>();

        @Override
        public String toString() {
            return "[" + nextStateName + "," + transitionEvent + "," + varsInspected + "," + variablesAssignment + "]";
        }
    }

    /**
     * Check all the variables in the context. Generate a state with a list of
     * variables correctly assigned
     *
     * @param possiblePositiveStates
     */
    private ArrayList<PossibleState> findPossibleStates() throws ModelException, SCXMLExpressionException, IOException {
        ArrayList<PossibleState> possiblePositiveStates = new ArrayList<PossibleState>();
        ArrayList<String> positive = new ArrayList<String>();
        ArrayList<String> negative = new ArrayList<String>();
        findEvents(positive, negative);
        for (String state : positive) {
            PossibleState possibleState = new PossibleState();
            String[] parts = state.split("-");
            possibleState.nextStateName = parts[2];
            possibleState.transitionEvent = parts[1];
            possiblePositiveStates.add(possibleState);
        }
        return possiblePositiveStates;
    }

    private static HashMap<String, String> readVarsOut() {
        HashMap<String, String> result = new HashMap<String, String>();
        for (String varName : varsOut) {
            result.put(varName, (String) context.get(varName));
        }
        return result;
    }

    private void traceDepth(ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList) throws ModelException, IOException, SCXMLExpressionException {
        resetStateMachine();
        while (listener.getCurrentState() == null
                || (listener.getCurrentState() != null && !listener.getCurrentState().getId().equals("end"))) {
            log.debug("Resetting state maching");
            resetStateMachine();

            // Replay the initial states
            for (ArrayList<PossibleState> states : possiblePositiveStatesList) {
                PossibleState initialState = states.get(0);
                // Fire the event of the state
                HashMap<String, String> varsVals = null;
                HashMap<String, String> nextVarsVals;
                if (!initialState.varsInspected) {
                    varsVals = readVarsOut();
                }
                try {
                    executor.triggerEvent(new TriggerEvent(initialState.transitionEvent, TriggerEvent.SIGNAL_EVENT));
                } catch (Exception e) {
                    throw new IOException("Exception while triggering transition event " + initialState.transitionEvent, e);
                }
                if (!initialState.varsInspected) {
                    nextVarsVals = readVarsOut();
                    if (varsVals == null || nextVarsVals == null) {
                        throw new IOException("NULL in nextVarsVals or varsVals");
                    }
                    for (Entry<String, String> var : varsVals.entrySet()) {
                        String nextVal = nextVarsVals.get(var.getKey());
                        if (!nextVal.equals(var.getValue())) {
                            if (nextVal.startsWith("set:{")) {
                                // Remove the set:{ and }
                                String[] vals = nextVal.substring(5, nextVal.length() - 1).split(",");

                                // Delete this state from the list
                                states.remove(0);
                                for (String val : vals) {
                                    PossibleState possibleState = new PossibleState();
                                    possibleState.nextStateName = initialState.nextStateName;
                                    possibleState.transitionEvent = initialState.transitionEvent;
                                    possibleState.variablesAssignment.putAll(initialState.variablesAssignment);
                                    possibleState.variablesAssignment.put(var.getKey(), val);
                                    possibleState.varsInspected = true;
                                    states.add(0, possibleState);
                                    log.debug("Adding:" + possibleState);
                                }
                            } else {
                                states.get(0).variablesAssignment.put(var.getKey(), nextVal);
                                states.get(0).varsInspected = true;
                            }
                        }
                    }
                    initialState = states.get(0);
                }

                // Set the variables
                for (Entry<String, String> var : initialState.variablesAssignment.entrySet()) {
                    context.set(var.getKey(), var.getValue());
                }
            }

            ArrayList<PossibleState> nextPositiveStates = findPossibleStates();

            possiblePositiveStatesList.add(nextPositiveStates);
        }

        // Remove end
        possiblePositiveStatesList.remove(possiblePositiveStatesList.size() - 1);
    }

    private final AtomicLong nextInt = new AtomicLong(0);

    private final String[] words = new String[]{
        "O'Rielly", "节日快乐", "bonnes fêtes", "חג שמח", "καλές διακοπές", "خوش تعطیلات", "खुश छुट्टियाँ"};
    // \uFFFE
    private final Random random = new Random(System.currentTimeMillis());
    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    private final SimpleDateFormat mfformat = new SimpleDateFormat("yyyyMMddHHmmssSS");
    private final SimpleDateFormat timeformat = new SimpleDateFormat("HHmmssSS");
    private long lines = 0;
    private long bytes = 0;
    private long manifestTimeMs = System.currentTimeMillis() - 1000 * 24 * 3600;

    private final String[] currencyCodes = {
        "AFG",
        "ALA",
        "ALB",
        "DZA",
        "ASM",
        "AND",
        "AGO",
        "AIA",
        "ATA",
        "ATG",
        "ARG",
        "ARM",
        "ABW",
        "AUS",
        "AUT",
        "AZE",
        "BHS",
        "BHR",
        "BGD",
        "BRB",
        "BLR",
        "BEL",
        "BLZ",
        "BEN",
        "BMU",
        "BTN",
        "BOL",
        "BES",
        "BIH",
        "BWA",
        "BVT",
        "BRA",
        "IOT",
        "BRN",
        "BGR",
        "BFA",
        "BDI",
        "KHM",
        "CMR",
        "CAN",
        "CPV",
        "CYM",
        "CAF",
        "TCD",
        "CHL",
        "CHN",
        "CXR",
        "CCK",
        "COL",
        "COM",
        "COG",
        "COD",
        "COK",
        "CRI",
        "CIV",
        "HRV",
        "CUB",
        "CUW",
        "CYP",
        "CZE",
        "DNK",
        "DJI",
        "DMA",
        "DOM",
        "ECU",
        "EGY",
        "SLV",
        "GNQ",
        "ERI",
        "EST",
        "ETH",
        "FRO",
        "FLK",
        "FJI",
        "FIN",
        "FRA",
        "GUF",
        "PYF",
        "ATF",
        "GAB",
        "GMB",
        "GEO",
        "DEU",
        "GHA",
        "GIB",
        "GBR",
        "GRC",
        "GRL",
        "GRD",
        "GLP",
        "GUM",
        "GTM",
        "GGY",
        "GIN",
        "GNB",
        "GUY",
        "HTI",
        "HMD",
        "HND",
        "HKG",
        "HUN",
        "ISL",
        "IND",
        "IDN",
        "IRN",
        "IRQ",
        "IRL",
        "IMN",
        "ISR",
        "ITA",
        "JAM",
        "JPN",
        "JEY",
        "JOR",
        "KAZ",
        "KEN",
        "KIR",
        "PRK",
        "KOR",
        "KWT",
        "KGZ",
        "LAO",
        "LVA",
        "LBN",
        "LSO",
        "LBR",
        "LBY",
        "LIE",
        "LTU",
        "LUX",
        "MAC",
        "MKD",
        "MDG",
        "MWI",
        "MYS",
        "MDV",
        "MLI",
        "MLT",
        "MHL",
        "MTQ",
        "MRT",
        "MUS",
        "MYT",
        "MEX",
        "FSM",
        "MDA",
        "MCO",
        "MNG",
        "MNE",
        "MSR",
        "MAR",
        "MOZ",
        "MMR",
        "NAM",
        "NRU",
        "NPL",
        "NLD",
        "ANT",
        "NCL",
        "NZL",
        "NIC",
        "NER",
        "NGA",
        "NIU",
        "NFK",
        "MNP",
        "NOR",
        "OMN",
        "PAK",
        "PLW",
        "PSE",
        "PAN",
        "PNG",
        "PRY",
        "PER",
        "PHL",
        "PCN",
        "POL",
        "PRT",
        "PRI",
        "QAT",
        "REU",
        "ROU",
        "RUS",
        "RWA",
        "BLM",
        "SHN",
        "KNA",
        "LCA",
        "MAF",
        "SPM",
        "VCT",
        "WSM",
        "SMR",
        "STP",
        "SAU",
        "SEN",
        "SRB",
        "SYC",
        "SLE",
        "SGP",
        "SXM",
        "SVK",
        "SVN",
        "SLB",
        "SOM",
        "ZAF",
        "SGS",
        "SSD",
        "ESP",
        "LKA",
        "SDN",
        "SUR",
        "SJM",
        "SWZ",
        "SWE",
        "CHE",
        "SYR",
        "TWN",
        "TJK",
        "TZA",
        "THA",
        "TLS",
        "TGO",
        "TKL",
        "TON",
        "TTO",
        "TUN",
        "TUR",
        "TKM",
        "TCA",
        "TUV",
        "UGA",
        "UKR",
        "ARE",
        "GBR",
        "USA",
        "UMI",
        "URY",
        "UZB",
        "VUT",
        "VAT",
        "VEN",
        "VNM",
        "VGB",
        "VIR",
        "WLF",
        "ESH",
        "YEM",
        "ZMB",
        "ZWE",};
    int nextCurrencyCode = 0;

    private final String[] productCodes = new String[]{
        "Equity- Private Placements/IPO",
        "Equity-Stock",
        "Equity-Foreign",
        "Fixed Income-Agency",
        "Fixed Income-Corporate",
        "Fixed Income-Government Securities",
        "Fixed Income-Municipal",
        "Fixed Income-Negotiable CDs",
        "Fixed Income-Private Placements",
        "Securitized Debt Instruments-Asset Backed Securities",
        "Securitized Debt Instruments-Mortgage Backed Securities",
        "Derivatives-Foreign Exchange",
        "Derivatives-Forwards",
        "Derivatives-Futures",
        "Derivatives-Options",
        "Derivatives-Rights",
        "Derivatives-Swaps",
        "Derivatives-Warrants",
        "Variable Products-Systematic Investment Plans",
        "Variable Products-Variable Annuities",
        "Variable Products-Variable Life Insurance",
        "Investment Company Products-Auction Rate Securities",
        "Investment Company Products-Closed -End Mutual Funds",
        "Investment Company Products-Exchange Traded Funds",
        "Investment Company Products-Open-End Mutual Funds",
        "Investment Company Products-Unit Investment Trusts",
        "Alternative Investments-Structured Products",};
    int nextProductCode = 0;

    private void produceOutput() throws IOException {
        String[] outTemplate = new String[]{
            /*  1 */"var_out_RECORD_TYPE",
            /*  3 */ "var_out_MANIFEST_GENERATION_DATETIME",
            /*  4 */ "var_out_ACCOUNT_NUMBER",
            /*  5 */ "var_out_TRADE_DATE",
            /*  6 */ "var_out_POSITION_TYPE_CODE",
            /*  7 */ "var_out_SECURITY_SYMBOL",
            /*  8 */ "var_out_OPTIONS_SYMBOLOGY_IDENTIFIER",
            /*  9 */ "var_out_CUSIP_IDENTIFIER",
            /* 10 */ "var_out_SEDOL_IDENTIFIER",
            /* 11 */ "var_out_ISIN_IDENTIFIER",
            /* 13 */ "var_out_INTERNAL_SECURITY_IDENTIFIER",
            /* 14 */ "var_out_PRODUCT_TYPE_CODE",
            /* 15 */ "var_out_BUY_SELL_TRANSACTION_CODE",
            /* 16 */ "var_out_OPTION_OPEN_CLOSE_INDICATOR",
            /* 17 */ "var_out_FIRM_CRD_NUMBER",
            /* 18 */ "var_out_INTERNAL_REPORTING_FIRM_IDENTIFIER",
            /* 19 */ "var_out_INTERNAL_REPORTING_BRANCH_IDENTIFIER",
            /* 20 */ "var_out_ORDER_ENTRY_DATE",
            /* 21 */ "var_out_EXECUTION_TIME",
            /* 22 */ "var_out_ORDER_ENTRY_TIME",
            /* 23 */ "var_out_TRANSACTION_QUANTITY",
            /* 25 */ "var_out_DAY_TRADING_FLAG",
            /* 26 */ "var_out_INTRA_DAY_HEDGE_CODE",
            /* 27 */ "var_out_WHEN_ISSUED_TRADE_FLAG",
            /* 28 */ "var_out_NET_AMOUNT",
            /* 29 */ "var_out_SETTLEMENT_DATE",
            /* 30 */ "var_out_EXECUTION_PRICE",
            /* 31 */ "var_out_TRADE_AS_OF_DATE",
            /* 32 */ "var_out_CURRENCY_TYPE_CODE",
            /* 33 */ "var_out_PRINCIPAL_AMOUNT",
            /* 34 */ "var_out_COMMISSION_AMOUNT",
            /* 35 */ "var_out_ORDER_NUMBER",
            /* 36 */ "var_out_ORDER_TRANSACTION_CODE",
            /* 37 */ "var_out_SOLICITATION_TYPE_CODE",
            /* 38 */ "var_out_TRANSACTION_CAPACITY_CODE",
            /* 39 */ "var_out_COMMISSION_PRE_FIGURED_FLAG",
            /* 40 */ "var_out_MARKUP_MARKDOWN_AMOUNT",
            /* 41 */ "var_out_FEE_SECTION_31_TRANSACTION_FEE",
            /* 42 */ "var_out_FEE_OPTION_REGULATORY_FEE",
            /* 43 */ "var_out_FEE_SERVICE_CHARGE",
            /* 44 */ "var_out_ACCOUNT_INVESTMENT_HORIZON_DESCRIPTION",
            /* 45 */ "var_out_ACCOUNT_MARGIN_MAITENANCE_REQUIREMENT_PERCENTAGE",
            /* 46 */ "var_out_AVERAGE_PRICE_TRANSACTION_FLAG",
            /* 47 */ "var_out_CLEARING_FIRM_TRADE_REFERENCE_NUMBER",
            /* 48 */ "var_out_CONTINGENT_DEFERRED_SALES_CHARGECONTINGENT_DEFERRED_SALES_CHARGE",
            /* 49 */ "var_out_CONTRA_ACCOUNT_NUMBER",
            /* 50 */ "var_out_CONTRA_CRD_NUMBER",
            /* 51 */ "var_out_DISCRETIONARY_FLAG",
            /* 52 */ "var_out_DIVIDENDS_REINVESTED_FLAG",
            /* 53 */ "var_out_EXCHANGE_CODE",
            /* 54 */ "var_out_FEE_OTHER",
            /* 55 */ "var_out_LETTER_OF_INTENT_DATE",
            /* 56 */ "var_out_LETTER_OF_INTENT_VALUE_AMOUNT",
            /* 57 */ "var_out_MARGIN_CALL_REFERENCE_NUMBER",
            /* 58 */ "var_out_MUTUAL_FUND_SHARE_CLASS",
            /* 59 */ "var_out_ORDER_EXECUTION_SEQUENCE_NUMBER",
            /* 60 */ "var_out_PS_REFERENCE_NUMBER",
            /* 61 */ "var_out_PRIVATE_SECURITES_TRANSACTION_FLAG",
            /* 62 */ "var_out_PUBLIC_OFFERING_PRICE",
            /* 63 */ "var_out_RIGHTS_OF_ACCUMULATION_VALUE_AMOUNT",
            /* 64 */ "var_out_SECURITY_DESCRIPTION",
            /* 65 */ "var_out_TRADE_AWAY_STEP_OUT_FLAG",
            /* 66 */ "var_out_MARGIN_SELL_OUT_FLAG",
            /* 68 */ "var_out_REGISTERED_REP_INTERNAL_IDENTIFIER"
        };

        StringBuilder b = new StringBuilder();
        for (String var : outTemplate) {
            if (b.length() > 0) {
                b.append("|");
            }
            Object varObj = context.get(var);
            String val = "";
            if (varObj != null) {
                val = varObj.toString();
            }

            if (val.equals("#{genTransactionQuantity}")) {
                val = "PFLOAT(2,2)";
            }

            if (val.equals("#{genNetAmount}")) {
                val = "NUMBER(5,3)";
            }

            if (val.equals("#{genPrincipalAmount}")) {
                val = "NUMBER(18,5)";
            }

            if (val.equals("#ProductTypeCode_Cycle")) {
                String productCode = productCodes[(nextProductCode++) % productCodes.length];
                b.append(productCode);
            } else if (val.equals("#{genCurrencyCode}")) {
                String currencyCode = currencyCodes[(nextCurrencyCode++) % currencyCodes.length];
                b.append(currencyCode);
            } else if (val.startsWith("ALPHA(")) {
                int len = Integer.valueOf(val.substring(6, val.length() - 1));
                while (len > 0) {
                    int word = random.nextInt(words.length);
                    int letter = random.nextInt(words[word].length());
                    b.append(words[word].charAt(letter));
                    len--;
                }
            } else if (val.startsWith("NUMBER(")) {
                int wholeDigits;
                int fractions = 0;

                if (val.contains(",")) {
                    wholeDigits = Integer.valueOf(val.substring(7, val.indexOf(",")));
                    fractions = Integer.valueOf(val.substring(val.indexOf(",") + 1,
                            val.length() - 1));
                } else {
                    wholeDigits = Integer.valueOf(val.substring(7, val.length() - 1));
                }

                if (wholeDigits > 2 && random.nextInt(1000) > 950) {
                    b.append("-");
                    wholeDigits--;
                }

                while (wholeDigits > 0) {
                    b.append(random.nextInt(10));
                    wholeDigits--;
                }

                if (fractions > 0) {
                    b.append('.');
                    while (fractions > 0) {
                        b.append(random.nextInt(10));
                        fractions--;
                    }
                }
            } else if (val.startsWith("PFLOAT(")) {
                int wholeDigits;
                int fractions = 0;

                if (val.contains(",")) {
                    wholeDigits = Integer.valueOf(val.substring(7, val.indexOf(",")));
                    fractions = Integer.valueOf(val.substring(val.indexOf(",") + 1,
                            val.length() - 1));
                } else {
                    wholeDigits = Integer.valueOf(val.substring(7, val.length() - 1));
                }

                while (wholeDigits > 0) {
                    b.append(random.nextInt(10));
                    wholeDigits--;
                }

                if (fractions > 0) {
                    b.append('.');
                    while (fractions > 0) {
                        b.append(random.nextInt(10));
                        fractions--;
                    }
                }
            } else if (val.equals("#{randomyyyymmddhhmmsscc}")) {
                manifestTimeMs += random.nextInt(1000);
                String dateyyyymmddhhsscc = mfformat.format(manifestTimeMs);
                b.append(dateyyyymmddhhsscc);
            } else if (val.equals("#{nextint}")) {
                b.append(nextInt.incrementAndGet());
            } else if (val.equals("#{randomwords}")) {
                for (int i = 0; i != 3; i++) {
                    b.append(words[random.nextInt(words.length)])
                            .append(" ");
                }
            } else if (val.equals("#{randomyyyymmdd}")) {
                long randTime = System.currentTimeMillis() - random.nextInt(1000) * 3600 * 24 * 365;
                String dateyyyymmdd = format.format(new Date(randTime));
                b.append(dateyyyymmdd);
            } else if (val.equals("#{randomhhmmsscc}")) {
                long randTime = System.currentTimeMillis() - random.nextInt(1000) * 3600 * 24 * 365;
                String dateyyyymmdd = timeformat.format(new Date(randTime));
                b.append(dateyyyymmdd);
            } else {
                b.append(val);
            }
        }

        b.append("\n");
        String data = b.toString();
        bytes += data.getBytes().length;
        if (os != null) {
            os.write(data.getBytes());
        }

        if (sequenceFileWriter != null) {
            LongWritable key = new LongWritable(lines);
            Text text = new Text(data);
            sequenceFileWriter.append(key, text);
        }

        lines++;
        if (lines % 1000 == 0) {
            log.info("Wrote " + lines + " " + bytes + " bytes");
        }
    }

    /**
     * Do a depth first search looking for scenarios
     *
     * @throws ModelException
     * @throws SCXMLExpressionException
     * @throws IOException
     */
    private void searchForScenariosDFS() throws ModelException, SCXMLExpressionException, IOException {
        log.info("Search for scenarios using depth first search");
        ArrayList<ArrayList<PossibleState>> possiblePositiveStatesList = new ArrayList<ArrayList<PossibleState>>();
        ArrayList<String> currentStates = new ArrayList<String>();
        ArrayList<Integer> activePostiveState = new ArrayList<Integer>();

        // First we have to generate the first level in the depth, so that we have something to start
        // the recursion from
        log.debug("Searching for the initial next possible states");
        traceDepth(possiblePositiveStatesList);
        log.debug("Initial depth trace: " + possiblePositiveStatesList);

        // Now we have the initial list with sets decompressed
        produceOutput();
        while (true) {
            // Recursively delete one node from the end
            boolean empty;
            do {
                if (possiblePositiveStatesList.isEmpty()) {
                    empty = true;
                    break;
                }
                empty = false;
                int lastDepth = possiblePositiveStatesList.size() - 1;
                log.debug("Removing: " + possiblePositiveStatesList.get(lastDepth).remove(0).nextStateName);
                if (possiblePositiveStatesList.get(lastDepth).isEmpty()) {
                    log.debug("Empty at level: " + possiblePositiveStatesList.size());
                    possiblePositiveStatesList.remove(possiblePositiveStatesList.size() - 1);
                    empty = true;
                }
            } while (empty);

            if (empty) {
                // We're done
                break;
            }

            log.debug("**After removing, depth trace: " + possiblePositiveStatesList);
            traceDepth(possiblePositiveStatesList);
            log.debug("**After finding next, depth trace: " + possiblePositiveStatesList);

            produceOutput();
        }
    }

    private void searchForScenarios() throws ModelException, SCXMLExpressionException, IOException {
        ArrayDeque<ArrayList<String>> stack = new ArrayDeque<ArrayList<String>>();
        int numberOfScenariosGenerated = 0;

        ArrayList<String> positiveEvents = new ArrayList<String>();
        ArrayList<String> negativeEvents = new ArrayList<String>();

        resetStateMachine();
        findEvents(positiveEvents, negativeEvents);

        // Add every positive event by itself, since it will be executed after the initial ones
        for (String event : positiveEvents) {
            ArrayList<String> singleEventList = new ArrayList<String>();
            singleEventList.add(event);
            singleEventList = pruneEvents(singleEventList);

            if (singleEventList != null) {
                stack.push(singleEventList);
                printEvents("+ve:", singleEventList);
                numberOfScenariosGenerated++;
            }
        }

        // Check the -ve events
        for (String event : negativeEvents) {
            ArrayList<String> singleEventList = new ArrayList<String>();
            singleEventList.add(event);
            singleEventList = pruneEvents(singleEventList);

            if (singleEventList != null) {
                printEvents("-ve:", singleEventList);
                numberOfScenariosGenerated++;
            }
        }

        while (stack.size() > 0 && numberOfScenariosGenerated < maxScenarios) {
            ArrayList<String> scenario = stack.pop();

            log.debug("Searching for more scenarios using: " + scenario);

            resetStateMachine();
            fireEvents(scenario);
            findEvents(positiveEvents, negativeEvents);

            // Add every positive event by itself, since it will be executed after the initial ones
            for (String event : positiveEvents) {
                ArrayList<String> eventList = new ArrayList<String>();
                log.debug("Scenario:" + scenario + " new event:" + event);
                eventList.addAll(scenario);
                eventList.add(event);
                eventList = pruneEvents(eventList);

                if (eventList != null) {
                    stack.push(eventList);
                    printEvents("+ve:", eventList);
                    numberOfScenariosGenerated++;
                }
            }

            // Check the -ve events
            for (String event : negativeEvents) {
                ArrayList<String> eventList = new ArrayList<String>();
                eventList.addAll(scenario);
                eventList.add(event);
                eventList = pruneEvents(eventList);

                if (eventList != null) {
                    printEvents("-ve:", eventList);
                    numberOfScenariosGenerated++;
                }
            }
        }
    }

    /**
     * Delete the scenario if an event is repeated more than maxEventReps times
     *
     * @param eventList
     * @return
     */
    private ArrayList<String> pruneEvents(ArrayList<String> eventList) {
        // Count the number of repetitions of every event
        ArrayList<String> all = new ArrayList<String>();
        all.addAll(initialEventsList);
        all.addAll(eventList);

        if (all.size() > lengthOfScenario) {
            return null;
        }

        HashMap<String, Integer> count = new HashMap<String, Integer>();
        for (String event : all) {
            Integer counter = count.get(event);
            if (counter == null) {
                counter = 0;
            }
            counter++;
            count.put(event, counter);
            if (counter > maxEventReps) {
                return null;
            }
        }
        return eventList;
    }
}
