DG:Transform
===========

There is DG feature - "dg:transform” tag.

Features:
 * Applies a stated DataTransformer (given by name in “name” parameter ) against every possible state.

List of parameters:

+----------------+-------------------------------------------+--------------+--------+
| Parameter name | Description                               | Is required? | Example|
+----------------+-------------------------------------------+--------------+--------+
| name           | Name of property where data will be saved | yes          | EQ     |
+----------------+-------------------------------------------+--------------+--------+


Full scxml example:

    <scxml xmlns="http://www.w3.org/2005/07/scxml"
           xmlns:dg="org.finra.datagenerator"
           version="1.0"
           initial="start">

        <state id="start">
            <transition event=“transform” target="transform"/>
        </state>

        <state id="transform">
            <onentry>
                <assign name="var_1” expr=“#{customplaceholder}”/>
                <dg:transform name=“EQ”/>
            </onentry>
            <transition event="end" target="end"/>
        </state>

    <state id="end">
        </state>
    </scxml>
    
