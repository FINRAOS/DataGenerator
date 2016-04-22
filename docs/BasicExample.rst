Example: Eight Lonely Rooks
====================================

The Problem
-----------

The eight lonely rooks problem is a simple chess-based problem: place eight rooks on the chess board such that none of the rooks can attack each other. We can use DataGenerator to solve this problem, outputting every possible arrangement of rooks as test data for use elsewhere.

Modeling the Problem with SCXML
-------------------------------

To put eight rooks on a chess board such that none attack each other, all eight ranks and all eight files will have one rook on them. Knowing this simplifies the SCXML model we need to describe the problem::

    <scxml xmlns="http://www.w3.org/2005/07/scxml"
        xmlns:cs="http://commons.apache.org/scxml"
        xmlns:dg="org.finra.datagenerator"
        version="1.0" initial="start">

        <state id="start">
            <transition target="ROOK_ONE" event="ROOK_ONE" />
        </state>

        <state id="ROOK_ONE">
            <onentry>
                <assign name="rook_rank_one" expr="1" />
                <dg:assign name="rook_file_one" set="a,b,c,d,e,f,g,h" />
            </onentry>
            <transition target="ROOK_TWO" event="ROOK_TWO" />
        </state>

        <state id="ROOK_TWO">
            <onentry>
                <assign name="rook_rank_two" expr="2" />
                <dg:assign name="rook_file_two" set="a,b,c,d,e,f,g,h" />
            </onentry>
            <transition target="ROOK_THREE" event="ROOK_THREE" cond="${!(rook_file_two == rook_file_one)}" />
        </state>

        <state id="ROOK_THREE">
            <onentry>
                <assign name="rook_rank_three" expr="3" />
                <dg:assign name="rook_file_three" set="a,b,c,d,e,f,g,h" />
            </onentry>
            <transition target="ROOK_FOUR" event="ROOK_FOUR" cond="${!(rook_file_three == rook_file_two || rook_file_three == rook_file_one)}" />
        </state>

        <state id="ROOK_FOUR">
            <onentry>
                <assign name="rook_rank_four" expr="4" />
                <dg:assign name="rook_file_four" set="a,b,c,d,e,f,g,h" />
            </onentry>
            <transition target="ROOK_FIVE" event="ROOK_FIVE" cond="${!(rook_file_four == rook_file_three || rook_file_four == rook_file_two || rook_file_four == rook_file_one)}" />
        </state>

        <state id="ROOK_FIVE">
            <onentry>
                <assign name="rook_rank_five" expr="5" />
                <dg:assign name="rook_file_five" set="a,b,c,d,e,f,g,h" />
            </onentry>
            <transition target="ROOK_SIX" event="ROOK_SIX" cond="${!(rook_file_five == rook_file_four || rook_file_five == rook_file_three || rook_file_five == rook_file_two || rook_file_five == rook_file_one)}" />
        </state>

        <state id="ROOK_SIX">
            <onentry>
                <assign name="rook_rank_six" expr="6" />
                <dg:assign name="rook_file_six" set="a,b,c,d,e,f,g,h" />
            </onentry>
            <transition target="ROOK_SEVEN" event="ROOK_SEVEN" cond="${!(rook_file_six == rook_file_five || rook_file_six == rook_file_four || rook_file_six == rook_file_three || rook_file_six == rook_file_two || rook_file_six == rook_file_one)}" />		
        </state>

        <state id="ROOK_SEVEN">
            <onentry>
                <assign name="rook_rank_seven" expr="7" />
                <dg:assign name="rook_file_seven" set="a,b,c,d,e,f,g,h" />
            </onentry>
            <transition target="ROOK_EIGHT" event="ROOK_EIGHT" cond="${!(rook_file_seven == rook_file_six || rook_file_seven == rook_file_five || rook_file_seven == rook_file_four || rook_file_seven == rook_file_three || rook_file_seven == rook_file_two || rook_file_seven == rook_file_one)}" />	
        </state>

        <state id="ROOK_EIGHT">
            <onentry>
                <assign name="rook_rank_eight" expr="8" />
                <dg:assign name="rook_file_eight" set="a,b,c,d,e,f,g,h" />
            </onentry>
            <transition target="end" event="end" cond="${!(rook_file_eight == rook_file_seven || rook_file_eight == rook_file_six || rook_file_eight == rook_file_five || rook_file_eight == rook_file_four || rook_file_eight == rook_file_three || rook_file_eight == rook_file_two || rook_file_eight == rook_file_one)}" />	
        </state>

        <state id="end">
            <!-- We're done -->
        </state>
    </scxml>

The model assumes that rook number 'n' will be on the nth rank. A rook's placement only has degrees of freedom over the files. Expressed as SCXML, we have a state in the state machine for each rook. Each rook's state sets the rook's rank to the assumed value and sets the rook's file to be any of the eight files. For each rook, it requires that the rook's file not equal the files of any of the preceding rooks. During the search over the SCXML state machine, any search branches that assign two rooks the same rank will be pruned by a lack of forward transitions.

Solving with DataGenerator
--------------------------

The number of solutions to the eight lonely rooks problem is relatively small (8! or 40320 solutions), so multithreaded mode will suffice::

    import org.finra.datagenerator.consumer.DataConsumer;
    import org.finra.datagenerator.distributor.multithreaded.DefaultDistributor;
    import org.finra.datagenerator.engine.scxml.SCXMLEngine;
    import org.finra.datagenerator.engine.Engine;
    import java.io.InputStream;

    public class Main {

        public static void main(String[] args) throws Exception {
            //prepare engine
            Engine chartExec = new SCXMLEngine();

            InputStream is = Main.class.getResourceAsStream("/eightLonelyRooks.xml");
            chartExec.setModelByInputFileStream(is);

            chartExec.setBootstrapMin(64);

            //prepare the consumer
            DataConsumer consumer = new DataConsumer();
            consumer.addDataWriter(new RookWriter(System.out));

            //prepare the distributor
            DefaultDistributor dist = new DefaultDistributor();
            dist.setDataConsumer(consumer);
            dist.setThreadCount(10);

            chartExec.process(dist);
        }
    }

The Main class makes an SCXMLEngine, loads the SCXML state machine seen above from the resources folder, and requests a bootstrap of 64. Ten threads process the 64 Frontiers produced by the bootstrap, overseen by a DefaultDistributor. It uses the standard DataConsumer with a custom DataWriter that produces chess style notation instead of pipe delineated output::

    import org.finra.datagenerator.consumer.DataPipe;
    import org.finra.datagenerator.writer.DataWriter;

    import java.io.IOException;
    import java.io.OutputStream;

    public class RookWriter implements DataWriter {
        private OutputStream os;
        private String[] outTemplate = new String[]{"rook_file_one", "rook_file_two", "rook_file_three", "rook_file_four",
                "rook_file_five", "rook_file_six", "rook_file_seven", "rook_file_eight"};

        public RookWriter(OutputStream os) {
            this.os = os;
        }

        public void writeOutput(DataPipe cr) {
            StringBuilder b = new StringBuilder(1024);
            int index = 1;

            for (String var : outTemplate) {
                if (index > 1) {
                    b.append(' ');
                }
                b.append(cr.getDataMap().get(var));
                b.append(String.valueOf(index));
                index++;
            }

            try {
                os.write(b.toString().getBytes());
                os.write("\n".getBytes());
            } catch (IOException e) {

            }
        }
    }

Some resulting output, 10 lines of many thousand::

    a1 d2 b3 c4 e5 g6 h7 f8
    a1 d2 b3 c4 e5 h6 f7 g8
    a1 d2 b3 c4 e5 h6 g7 f8
    a1 g2 b3 c4 d5 e6 f7 h8
    a1 g2 b3 c4 d5 e6 h7 f8
    a1 d2 b3 c4 f5 e6 g7 h8
    a1 g2 b3 c4 d5 f6 e7 h8
    a1 g2 b3 c4 d5 f6 h7 e8
    a1 d2 b3 c4 f5 e6 h7 g8
    a1 g2 b3 c4 d5 h6 e7 f8

