Contributing
------------
We encourage contribution from the open source community to make DataGenerator better. Please refer to the [development](http://finraos.github.io/DataGenerator/index.html#get_involved) page for more information on how to contribute to this project.

Maven dependency
-----------------

```sh
<dependency>
    <groupId>org.finra.datagenerator</groupId>
    <artifactId>DataGenerator</artifactId>
    <version>1.0</version>
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


=======
Overview
=============

Data Generator generates pattern using two pieces of user provided information:

1. An SCXML state chart representing interactions between different states, and setting values to output variables
2. A user [Transformer]http://finraos.github.io/DataGenerator/apis/v2.0/org/finra/datagenerator/consumer/DataTransformer.html) that formats the variables and stores them.

The user can optionally provide their own [distributor](http://finraos.github.io/DataGenerator/apis/v2.0/org/finra/datagenerator/distributor/SearchDistributor.html) that distributes the search of bigger problems on systems like hadoop. By default, DataGenerator will use a multithreaded distributor.

=============
Quick start
=============

For the full coompilable code please see the [noconditions sample](https://github.com/FINRAOS/DataGenerator/tree/master/codesamples/noconditions)

First step, define an SCXML model:
```sh
<scxml xmlns="http://www.w3.org/2005/07/scxml"
       xmlns:cs="http://commons.apache.org/scxml"
       version="1.0"
       initial="start">

    <state id="start">
        <transition event="SETV1" target="SETV1"/>
    </state>

    <state id="SETV1">
        <onentry>
            <assign name="var_out_V1" expr="set:{A,B,C}"/>
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

This model contains three variables controlled by two states. The transition between those states is unconditional. The first variable var_out_V1 can have any of the values A,B and C. The second variable var_out_V2 can have any of the values 1,2 and 3. The third variable is set to a template that the user will replace with a custom value in a later stage.


