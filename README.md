[![Build Status](https://travis-ci.org/FINRAOS/DataGenerator.svg?branch=master)](https://travis-ci.org/FINRAOS/DataGenerator) [![Dependency Status](https://www.versioneye.com/user/projects/577671b068ee07003cb5d56a/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/577671b068ee07003cb5d56a) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/repos/FINRAOS/DataGenerator/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/repos/FINRAOS/DataGenerator/branches/master) [![Join the chat at https://gitter.im/FINRAOS/DataGenerator](https://badges.gitter.im/FINRAOS/DataGenerator.svg)](https://gitter.im/FINRAOS/DataGenerator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Quick Start Videos
------------------
https://www.youtube.com/playlist?list=PLB0Zha5q-7wJp3TLH782J7ZDQ2RwPS_hQ

Contributing
------------
We encourage contribution from the open source community to make DataGenerator better. Please refer to the [development](http://finraos.github.io/DataGenerator/index.html#get_involved) page for more information on how to contribute to this project.

Maven Dependency
-----------------
For the core

```sh
<dependency>
    <groupId>org.finra.datagenerator</groupId>
    <artifactId>dg-core</artifactId>
    <version>2.2</version>
</dependency>
```

For the commons library

```sh
<dependency>
    <groupId>org.finra.datagenerator</groupId>
    <artifactId>dg-common<artifactId>
    <version>2.2</version>
</dependency>
```

Building
------------
DataGenerator uses Maven for build. Please install Maven by downloading it from [here](http://maven.apache.org/download.cgi).

```sh
# Clone DataGenerator git repo
git clone git://github.com/FINRAOS/DataGenerator.git
cd DataGenerator

# Checkout master branch
git checkout master

# Run package to compile and create jar (also runs unit tests)
mvn package

# Compile and run unit tests only
mvn test
```


License
------------------------------------
The DataGenerator project is licensed under [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Overview
--------------------

Data Generator generates pattern using two pieces of user provided information:

1. An [SCXML](http://commons.apache.org/proper/commons-scxml/) state chart representing interactions between different states, and setting values to output variables
2. A user [Transformer](http://finraos.github.io/DataGenerator/apis/v2.0/org/finra/datagenerator/consumer/DataTransformer.html) that formats the variables and stores them.

The user can optionally provide their own [distributor](http://finraos.github.io/DataGenerator/apis/v2.0/org/finra/datagenerator/distributor/SearchDistributor.html) that distributes the search of bigger problems on systems like hadoop. By default, DataGenerator will use a multithreaded distributor.

Quick start
--------------------

For the full compilable code please see the [default example](https://github.com/FINRAOS/DataGenerator/blob/master/dg-example-default/)

First step, define an [SCXML](http://commons.apache.org/proper/commons-scxml/) model:
```xml
<scxml xmlns="http://www.w3.org/2005/07/scxml"
       xmlns:cs="http://commons.apache.org/scxml"
       version="1.0"
       initial="start">

    <state id="start">
        <transition event="SETV1" target="SETV1"/>
    </state>

    <state id="SETV1">
        <onentry>
            <assign name="var_out_V1_1" expr="set:{A1,B1,C1}"/>
            <assign name="var_out_V1_2" expr="set:{A2,B2,C2}"/>
            <assign name="var_out_V1_3" expr="77"/>
        </onentry>
        <transition event="SETV2" target="SETV2"/>
    </state>

    <state id="SETV2">
        <onentry>
            <assign name="var_out_V2" expr="set:{1,2,3}"/>
            <assign name="var_out_V3" expr="#{customplaceholder}"/>
        </onentry>
        <transition event="end" target="end"/>
    </state>

    <state id="end">
        <!-- We're done -->
    </state>
</scxml>
```

This model contains five variables controlled by two states. The transition between those states is unconditional. One of those variables is always constant ( var_out_V1_3 ). Three will acquire every value from a set ( var_out_V1_1, var_out_V1_2 and var_out_V2 ). var_out_V3 will be set to a holder value that will be replaced by the user at a later point.

The second step will be to write a [Transformer](http://finraos.github.io/DataGenerator/apis/v2.0/org/finra/datagenerator/consumer/DataTransformer.html). The code is [here](https://github.com/FINRAOS/DataGenerator/blob/master/dg-example-default/src/main/java/org/finra/datagenerator/samples/transformer/SampleMachineTransformer.java)

```java
public class SampleMachineTransformer implements DataTransformer {

    private static final Logger log = Logger.getLogger(SampleMachineTransformer.class);
    private final Random rand = new Random(System.currentTimeMillis());

    /**
     * The transform method for this DataTransformer
     * @param cr a reference to DataPipe from which to read the current map
     */
    public void transform(DataPipe cr) {
        for (Map.Entry<String, String> entry : cr.getDataMap().entrySet()) {
            String value = entry.getValue();

            if (value.equals("#{customplaceholder}")) {
                // Generate a random number
                int ran = rand.nextInt();
                entry.setValue(String.valueOf(ran));
            }
        }
    }

}
```
The above transformer will intercept every generated row, and convert the place holder "#customplaceholder" with a random number.

The last step will be writing a main function that ties both pieces together. Code is [here](https://github.com/FINRAOS/DataGenerator/blob/master/dg-example-default/src/main/java/org/finra/datagenerator/samples/CmdLine.java)
```java
    public static void main(String[] args) {

        Engine engine = new SCXMLEngine();

        //will default to samplemachine, but you could specify a different file if you choose to
        InputStream is = CmdLine.class.getResourceAsStream("/" + (args.length == 0 ? "samplemachine" : args[0]) + ".xml");

        engine.setModelByInputFileStream(is);

        // Usually, this should be more than the number of threads you intend to run
        engine.setBootstrapMin(1);

        //Prepare the consumer with the proper writer and transformer
        DataConsumer consumer = new DataConsumer();
        consumer.addDataTransformer(new SampleMachineTransformer());
        consumer.addDataWriter(new DefaultWriter(System.out,
                new String[]{"var_out_V1_1", "var_out_V1_2", "var_out_V1_3", "var_out_V2", "var_out_V3"}));

        //Prepare the distributor
        DefaultDistributor defaultDistributor = new DefaultDistributor();
        defaultDistributor.setThreadCount(1);
        defaultDistributor.setDataConsumer(consumer);
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        engine.process(defaultDistributor);
    }
```
The first few lines will open an input stream on the [SCXML](http://commons.apache.org/proper/commons-scxml/) file and pass the stream to the engine. Calling setBootStrapMin will attempt to split the graph generated from the state chart to at least the given number of splits. Here we passed 1 but in case you will execute the same code over hadoop or use a multithreaded version, you will need to increase that number to be at least the number of threads or mappers you wish to run. The rest of the code will set our transformer to the engine and create a writer based on the DefaultWriter. The function of the writer is to write the output to the user's desired destination.

The final piece sets the number of threads and called engine.process.
