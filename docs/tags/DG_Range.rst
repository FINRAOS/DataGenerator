DG:Range
===========

There is DG feature - "dg:range" tag.

Features:
 * Can generate range of positive / negative BigDecimals
 * Default step is '1'

List of parameters:

+----------------+--------------------------------------------------------------------------+--------------+--------+
| Parameter name | Description                                                              | Is required? | Example|
+----------------+--------------------------------------------------------------------------+--------------+--------+
| name           | Name of property where data will be saved                                | yes          | var_1  |
+----------------+--------------------------------------------------------------------------+--------------+--------+
| from           | Start value                                                              | yes          | 0      |
+----------------+--------------------------------------------------------------------------+--------------+--------+
| to             | Stop value                                                               | yes          | 15     |
+----------------+--------------------------------------------------------------------------+--------------+--------+
| step           | Step value. Can be positive and negative. Default value = 1              | no           | 0.5    |
+----------------+--------------------------------------------------------------------------+--------------+--------+


Full scxml example:

    <scxml xmlns="http://www.w3.org/2005/07/scxml"
           xmlns:dg="org.finra.datagenerator"
           version="1.0"
           initial="start">

        <state id="start">
            <transition event="1_rangeWithoutStep" target="1_rangeWithoutStep"/>
        </state>

        <state id="1_rangeWithoutStep">
            <onentry>
                <dg:range name="var_1" from="0" to="2"/>
            </onentry>
            <transition event="2_rangeWithStep" target="2_rangeWithStep" />
        </state>

        <state id="2_rangeWithStep">
            <onentry>
                <dg:range name="var_2" from="0" to="1" step="0.5" />
            </onentry>
            <transition event="end" target="end"/>
        </state>

        <state id="end"/>
    </scxml>
