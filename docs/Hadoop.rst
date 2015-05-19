Hadoop Usage
===============================

Overview
--------

Using DataGenerator with Hadoop is similar to using the DataGenerator multithreaded. All that changes is distributing the Frontiers produced by SCXMLEngine.

Prepare an SCXMLEngine as one would for using DataGenerator multithreaded, setting the model by String or InputStream and setting the desired amount of bootstrap. Then, instead of having SCXMLEngine process with a DefaultDistributor, process with a custom HadoopDistributor. This HadoopDistributor will distribute the Frontiers by making a Hadoop mapper for each Frontier and having the mappers perform the subsequent searchs on the Frontiers. The easiest way is to have each mapper make its own DefaultDistributor to handle the search. In essence one is splitting the distributing of Frontiers across many DefaultDistributors on many machines.    

The dg-example-hadoop module provides a complete demonstration, following the outline above, of using DataGenerator in conjunction with Hadoop.

SCXMLGapper
-----------

The custom HadoopDistributor can not directly give Frontiers to the mappers; memory is not shared between them. The HadoopDistributor must serialize the Frontiers into lines of text and write these lines out as input problems for the mappers. Mappers will have to rebuild their respective Frontiers from these serialized lines. The SCXMLGapper class makes serializng and rebuilding Frontiers easy; consult dg-example-hadoop to see it in use.

ContextWriter
-------------

ContextWriter is a DataWriter implementation that will write output lines to an Hadoop provided Context. The class is usable as is and can be found in dg-example-hadoop. The mapper will need to pass the Context to the desired DataConsumer to make the ContextWriter with.

WorkManager
-----------

None of the individual mappers can directly communicate with one another, which can be problematic if they need to share resources or have collective stopping conditions like an overall line count target.

A WorkManager is a class that establishes a server to handle communications with mappers. A WorkManager can provide mappers with resources or status updates the mappers need, can handle status updates and requests from the mappers, and can provide the user with progress reports on the mappers.

Communicating with the WorkManager
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

DataConsumers (each mapper and its DefaultDistributor should have one) can communicate with the WorkManager using the DataConsumer's sendRequest and sendRequestSync methods. What requests to send to the WorkManager, and what to expect as a response depends on the WorkManager.

Once instantiated the WorkManager method prepareServer() will make the WorkManager start listening for DataConsumer requests. The method prepareStatus() will make the WorkManager provide status reports on the mappers.

DataConsumers must be told the reporting host that corresponds to the WorkManager in order to communicate with the WorkManager. The getListeningPort() and getHostName() methods of WorkManager will provide the needed information.

Default WorkManagers
~~~~~~~~~~~~~~~~~~~~

The dg-example-hadoop module comes with two interfaces, WorkManager and WorkBlock, the JettyManager implementation of WorkManager, its extension LineManager, and the LineBlock implementation of WorkBlock.

JettyManager uses Jetty as the server to handle communications. LineManager extends JettyManager to handle an overall line count target, telling mappers when to shutdown based on the total number of lines all mappers have written.

Make Your Own WorkManager
~~~~~~~~~~~~~~~~~~~~~~~~~

One can make their own WorkManager using their desired server or to manage any desired kind of shared resource between the mappers. JettyManager is agnostic towards the kind of WorkBlock it manages. If jetty is an acceptable server, new resources can be managed with a simple WorkBlock implementation, just like LineBlock.
 
