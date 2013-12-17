Contributing
------------
We encourage contribution from the open source community to make DataGenerator better. Please refer to the [development](http://finraos.github.io/DataGenerator/index.html#get_involved) page for more information on how to contribute to this project.


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

DataGenerator Class architecture diagram
------------------------------------------------
![DataGenerator class diagram](http://finraos.github.io/DataGenerator/imgs/DataGenClassDiagram.png)

(**NOTE:** This does not include all classes included in the DataGenerator package)


License Type
------------------------------------
The DataGenerator project is licensed under [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)



=======
DataGenerator
=============

Primary goals of test automation should be increased coverage, decreased manual effort and flexibility to accommodate changing requirements.  DataGenerator is a new testing tool that supports these goals. This tool supports requirements specification and test case specification in terms of work  flows and data equivalence classes. By using templates, the format of the test data produced is highly customizable, and has the ability to include  dynamically calculated test expectations. The combined use of straightforward specification inputs, tool-assured test case coverage, and  customizable output enables a tester to achieve increased coverage in less time, while maintaining the flexibility to adapt to requirements changes.  In an agile software development environment, modifications to the specification input and output templates are all that’s required to get the tests re-generated.  This reduces time spent on test case generation and requirements gathering and alleviates gaps between requirements and test cases generated which  strengthens behavior-driven development.
