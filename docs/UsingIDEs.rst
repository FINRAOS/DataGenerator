Introduction
============

Eclipse
-------

To open the project, select "Existing Maven Projects" under Maven, hit Next. Point to DataGenerator folder and hit Next. Eclipse will complain about the jacoco plugin with a note "Resolve later". Click Finish.

A dialog box will pop up "Contuing will import projects with build error". Click Ok.

Eclipse may request to install additional items for ChecStyle and PMD. Install the requested plugins.

In the "Problems" tab, you should see four errors related to the Jacoco maven plugin. This is due to the fact that eclipse does not know how to execute Jacoco within its build process.

Select one of them and click Ctrl+1. Select "Permenantly mark goal prepare-agent in pom.xml as ignored in Eclipse build", and choose dg-parent. Repeat for the other errors (if needed).

IntelliJ Idea or NetBeans
-------------------------

To open the project in IntelliJ Idea or netbeans select open project, and point to the .pom file. The project opens in both editors without compaint
