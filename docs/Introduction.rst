Introduction
============

Data Generator (DG) is a library for systematically producing large volumes of data. DG frames data generation as a modeling problem,
with a user providing a model of dependencies among variables and the library traversing the model to produce relevant data sets. Our
built-in implementations build on open standards like `SCXMLDataModels <SCXMLDataModels.rst>`_ for modeling and Java threads for distributed generation to get users
up and running quickly - but everything in DG is completely customizable. As of version 2.0, choices for underlying model, underlying
execution architecture, generation technique, data transformations, and output formatting can all be described in Java code.

DG 2.0 has been intentionally designed to support the data generation challenges of the Big Data domain. Currently, our
`Hadoop <Hadoop.rst>`_ documentation describes the Hadoop sample implementation available in our repository on Github. In the future, we plan
to provide a library of Hadoop-specific implemenations for even easier adoption of Data Generator on this platform.

Users of Data Generator should be able to focus on the core issues of their specific data generation challenges, leaving less-important
details abstracted away unless additional customization is necessary. In the long term, we envision DG supporting a community
of developers tired of re-inventing the wheel, who can share implementations of various generation approaches with others facing
the same challenges.

Our project is open-sourced under the Apache 2.0 license. We welcome contributions from anyone interested in using or enhancing the tool
through our Github site (http://www.github.com/FINRAOS/DataGenerator).
