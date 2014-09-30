DataConsumer
============

DataConsumer handles final processing of the search results produced by a SearchDistributor (such as the DefaultDistributor).

Configuring a DataConsumer
--------------------------

A newly instantiated DataConsumer needs to be configured before it can be used.

DataPipes
~~~~~~~~~

The DataPipe class serves as a wrapper and formater for the search results produced by a SearchDistributor. DataPipe contains a Map<String, String> which maps variables in the SCXML state machine to their assignments. DataPipe's getPipeDelimited(String[] outTemplate) method returns a string of variable assignments separated by the pipe character "|" and ordered by the order of variable names in outTemplate.

DataTransformers
~~~~~~~~~~~~~~~~

DataTransformers alter the assigned values of variables stored in DataPipes. They are useful for replacing macros with proper values. Use addDataTransformer(DataTransformer dc) to add DataTransformers to a DataConsumer. A DataConsumer can have any number of DataTransformers.

DataWriters
~~~~~~~~~~~

DataWriters perform the writing of results to output. DataGenerator comes with the DefaultWriter class which writes to a provided OutputStream, such as System.out. Use addDataWriter(DataWriter ow) to add DataWriters to a DataConsumer. A DataConsumer can have any number of DataWriters, but will need at least one to be useful.

Consuming Search Results
~~~~~~~~~~~~~~~~~~~~~~~~

Search results are passed by the SearchDistributor to DataConsumer's consume(Map<String, String> initialVars) as a Map of variables to their assignments. DataConsumer processes each search result with three steps, with step 3 producing the actual output.

1. Makes a new DataPipe for the input Map
2. Succsessively apply each DataTransformer to the DataPipe in the order they were added
3. Succsessively apply each DataWriter to the DataPipe in the order they were added

Extending DataConsumer
----------------------

DataConsumer can be extended to provide additional or different functionality, particularly in what consume(Map<String, String> initialVars) does with search results.

Line Count
~~~~~~~~~~

The consume(Map<String, String> initialVars) method returns an integer representing the number of lines of output the DataConsumer produced. The SearchDistributor uses this information to stop the search upon reaching the desired number of output lines. User extended consumers should obey this behavior.

Exit Flag
~~~~~~~~~

Every DataConsumer has an AtomicBoolean exit flag which is provided to the DataConsumer by the SearchDistributor. When the flag is set to true by the DataConsumer, the SearchDistributor will halt searching, and no more search results will be passed to the DataConsumer for consumption. DataConsumer does not make use of this flag, but it is there for user extended consumers to use if needed.