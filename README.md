[DataGenerator](http://finraos.github.io/DataGenerator/index.html#get_involved)
=============

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
![DataGenerator Class Diagram](http://finraos.github.io/DataGenerator/imgs/DataGenClassDiagram.png)

(**NOTE:** This does not include all classes included in the DataGenerator package)


License Type
------------------------------------
The DataGenerator project is licensed under [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)



