DG:File
===========

There is DG feature - "dg:file" tag.

Features:
 * Lines started with comment ('#') will be ignored
 * Can read data from:
   * one line csv file. You can specify separator symbol. 
   * multi lines files (one value per row)

List of parameters:

+----------------+--------------------------------------------------------------------------------------------------------------------------------------+--------------+-----------------------+
| Parameter name | Description                                                                                                                          | Is required? | Example               |
+----------------+--------------------------------------------------------------------------------------------------------------------------------------+--------------+-----------------------+
| name           | Name of property where data will be saved                                                                                            | yes          | var_1                 |
+----------------+--------------------------------------------------------------------------------------------------------------------------------------+--------------+-----------------------+
| fileName       | File name. File has be available from classpath. It's recommended to use 'src/test/resources' folder to save all your test resources | yes          |   DGFileTest_step1.csv|
+----------------+--------------------------------------------------------------------------------------------------------------------------------------+--------------+-----------------------+
| separator      | Data separator. This parameter will be used if we have one line with data in file                                                    | no           | ','                   |
+----------------+--------------------------------------------------------------------------------------------------------------------------------------+--------------+-----------------------+


Data files example:

+------------+-----------------+-----------------------------------------------------------------------------+
| File type  | Example of data | scxml command                                                               |
+------------+-----------------+-----------------------------------------------------------------------------+
|csv         | param1,param2   | <dg:file name="var_1" fileName="DGFileTest_step1.csv" separator=","/>       |
+------------+-----------------+-----------------------------------------------------------------------------+
|multi lines | a1              | <dg:file name="var_2" fileName="DGFileTest_step2.txt" />                    |
|            | a2              |                                                                             |
|            | a3              |                                                                             |
+------------+-----------------+-----------------------------------------------------------------------------+

These two example will produce same results. You can select more suitable for your data format.


Full scxml example:

    <scxml xmlns="http://www.w3.org/2005/07/scxml"
           xmlns:dg="org.finra.datagenerator"
           version="1.0"
           initial="start">

        <state id="start">
            <transition event="1_fileWithSeparator" target="1_fileWithSeparator"/>
        </state>

        <state id="1_fileWithSeparator">
            <onentry>
                <dg:file name="var_1" fileName="DGFileTest_step1.csv" separator=","/>
            </onentry>
            <transition event="2_fileWithoutSeparator" target="2_fileWithoutSeparator"/>
        </state>

        <state id="2_fileWithoutSeparator">
            <onentry>
                <dg:file name="var_2" fileName="DGFileTest_step2.txt" />
            </onentry>
            <transition event="end" target="end"/>
        </state>

        <state id="end"/>
    </scxml>
