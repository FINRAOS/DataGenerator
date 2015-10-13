Assign
===========

There is scxml feature - "assign" tag.

See http://commons.apache.org/proper/commons-scxml/guide/datamodel.html

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
                <assign name="var_1" expr="Lorem"/>
            </onentry>
            <transition event="end" target="end"/>
        </state>

    <state id="end">
        </state>
    </scxml>