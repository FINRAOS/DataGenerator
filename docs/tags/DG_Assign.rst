DG:Assign
===========

There is DG feature - "dg:assign" tag.

Features:
 * Can assign multiple values in just one scxml state!

List of parameters:

+----------------+-------------------------------------------+--------------+--------+
| Parameter name | Description                               | Is required? | Example|
+----------------+-------------------------------------------+--------------+--------+
| name           | Name of property where data will be saved | yes          | var_1  |
+----------------+-------------------------------------------+--------------+--------+
| set            | Comma (',') separated values              | yes          |   a1,b1|
+----------------+-------------------------------------------+--------------+--------+

Full scxml example:

    <scxml xmlns="http://www.w3.org/2005/07/scxml"
           xmlns:dg="org.finra.datagenerator"
           version="1.0"
           initial="start">

        <state id="start">
            <transition event="BULK_ASSIGN" target="BULK_ASSIGN"/>
        </state>

        <state id="BULK_ASSIGN">
            <onentry>
                <dg:assign name="var_out_RECORD_TYPE" set="a1,b1,c1,d1,e1,f1,g1"/>
                <dg:assign name="var_out_RECORD_TYPE_2" set="a2,b2,c2,d2,e2,f2,g2"/>
            </onentry>
            <transition event="end" target="end"/>
        </state>

    <state id="end">
        </state>
    </scxml>