Multithreaded Usage
===================

There are three steps to use DataGenerator in multithreaded mode with an SCXML Model.

1. Instantiate and configure an SCXMLEngine.

2. Instantiate and configure a DefaultDistributor.

3. Have the SCXMLEngine process(SearchDistributor distributor) with the DefaultDistributor.

SCXMLEngine
-----------

SCXMLEngine performs the initial breadth first search on ones state machine to help bootstrap further depth first searching. To configure the SCXMLEngine one needs to set the state machine and the desired amount of bootstrapping. Bootstrapping results are stored into instances of SCXMLFrontier, which implements the Frontier interface, and are passed by the process(SearchDistributor distributor) method to the DefaultDistributor.

Set the State Machine
~~~~~~~~~~~~~~~~~~~~~

State machines need to be in the SCXML format and can be set with the SCXMLEngine methods setModelByInputFileStream(InputStream inputFileStream) or setModelByText(String model).

Set the Desired Bootstrapping
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The desired amount of bootstrapping is set with the SCXMLEngine method setBootstrapMin(int min). As the method name implies, it is the minimum amount of bootstrapping the SCXMLEngine will produce. The engine will try to produce the smallest amount of bootstrapping greater than or equal to the desired bootstrapping, but the SCXMLEngine can produce more.

DefaultDistributor
------------------

DefaultDistributor oversees the subsequent depth first search on ones state machine, using SearchWorker threads to run the depth first search in parallel, each processing a Frontier produced by the SCXMLEngine. To configure the DefaultDistributor one needs to set the DataConsumer, the desired number of output lines to write, and the desired number of SearchWorker threads to use.

Set the DataConsumer
~~~~~~~~~~~~~~~~~~~~

The DataConsumer handles final processing of the results produced from the depth first searching and handles the actual writing of output. The desired DataConsumer to use with the DefaultDistributor is set with the DefaultDistributor method setDataConsumer(DataConsumer dataConsumer). The page on DataConsumers, DataWriters, and DataTransformers explains how to configure the DataConsumer itself.

Set the Desired Number of Output Lines
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The desired amount of bootstrapping is set with the DefaultDistributor method setMaxNumberOfLines(long numberOfLines).

Set the Desired Number of SearchWorkers
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The desired amount of SearchWorker threads is set with the DefaultDistributor methodsetThreadCount(int threadCount). The number of SearchWorker threads used does not have to be same as the requested bootstrap amount. Individual SearchWorker threads can process more than one Frontier, searching a new Frontier when finished with their current one. One SearchWorker thread can handle an arbitrary amount of Frontiers if needed.
