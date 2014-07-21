package org.finra.datagenerator.distributor.multithreaded;

import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;
import org.finra.datagenerator.consumer.DataConsumer;
import org.finra.datagenerator.consumer.defaults.ChainConsumer;
import org.finra.datagenerator.consumer.defaults.ConsumerResult;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.distributor.SearchProblem;
import org.finra.datagenerator.writer.DefaultWriter;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by robbinbr on 3/24/14.
 */
public class DefaultDistributor implements SearchDistributor {
    protected static final Logger log = Logger.getLogger(DefaultDistributor.class);

    private int threadCount = 1;
    private final Queue<HashMap<String, String>> queue = new ConcurrentLinkedQueue<HashMap<String, String>>();
    private Thread outputThread;
    private DataConsumer userDataOutput;
    private String stateMachineText;
    private Map<String, AtomicBoolean> flags = new HashMap<String, AtomicBoolean>();
    private long maxNumberOfLines = -1;

    public DefaultDistributor() {
        ChainConsumer cc = new ChainConsumer();
        cc.addOutputWriter(new DefaultWriter(System.out));
        this.userDataOutput = cc;
    }

    public DefaultDistributor setDataConsumer(DataConsumer dataConsumer) {
        this.userDataOutput = dataConsumer;
        return this;
    }

    public DefaultDistributor setMaxNumberOfLines(long numberOfLines) {
        this.maxNumberOfLines = numberOfLines;
        return this;
    }

    public DefaultDistributor setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    @Override
    public SearchDistributor setStateMachineText(String stateMachine) {
        this.stateMachineText = stateMachine;
        return this;
    }

    @Override
    public void distribute(List<SearchProblem> searchProblemList) {

        // Start output thread (consumer)
        outputThread = new Thread() {
            @Override
            public void run() {
                try {
                    produceOutput();
                } catch (IOException ex) {
                    log.error("Error during output", ex);
                }
            }
        };
        outputThread.start();

        // Start search threads (producers)
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
        for (SearchProblem problem : searchProblemList) {
            Runnable worker = null;
            try {
                worker = new SearchWorker(problem, stateMachineText, queue, flags);
                threadPool.execute(worker);
            } catch (ModelException e) {
                log.error("Error while initializing SearchWorker threads", e);
            } catch (SAXException e) {
                log.error("Error while initializing SearchWorker threads", e);
            } catch (IOException e) {
                log.error("Error while initializing SearchWorker threads", e);
            }
        }

        threadPool.shutdown();
        try {
            // Wait for exit
            while (!threadPool.isTerminated()) {
                log.debug("Waiting for threadpool to terminate");
                Thread.sleep(1000);
            }

            while (!isSomeFlagTrue(flags)) {
                log.debug("Waiting for exit...");
                Thread.sleep(1000);
            }

            // Now, wait for the output thread to get done
            outputThread.join();
            log.info("DONE");
        } catch (InterruptedException ex) {
            log.info("Interrupted !!... exiting", ex);
        }
    }

    private void produceOutput() throws IOException {
        long lines = 0;
		while (!Thread.interrupted() && (!flags.containsKey("exitNow") || !flags.get("exitNow").get())) {
			HashMap<String, String> row = queue.poll();
			if (row != null) {
				ConsumerResult cr = new ConsumerResult(maxNumberOfLines, flags);
				for (Map.Entry<String, String> ent : row.entrySet()) {
					cr.getDataMap().put(ent.getKey(), ent.getValue());
				}

				userDataOutput.consume(cr);
				lines++;
			} else {
				if (flags.containsKey("exit") || flags.containsKey("exitNow")) {
					break;
				}
			}

			if (maxNumberOfLines != -1 && lines >= maxNumberOfLines) {
				flags.put("exitNow", new AtomicBoolean(true));
				break;
			}
		}

		if (null != flags && flags.containsKey("exit") && flags.get("exit").get()) {
			log.info("Exiting, exit flag ('exit') is true");
		}
    }

    public static boolean isSomeFlagTrue(Map<String, AtomicBoolean> flags) {
        for (AtomicBoolean flag : flags.values()) {
        	if (flag.get()) {
        		return true;
        	}
        }
        return false;
    }
    
    @Override
	public void setFlag(String name, AtomicBoolean flag) {
		flags.put(name, flag);
	}
}