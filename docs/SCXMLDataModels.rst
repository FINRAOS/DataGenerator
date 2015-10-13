SCXML Data Models
=================

Overview
--------

DataGenerator uses state machines defined in SCXML to produce output data. DataGenerator searches over the state machine to find all the paths from the start state to the end state, and commands encountered on a path from start to end define values for a set of variables, which composes a search result. These search results are processed afterward into the final output data. 

SCXML Wrapper
~~~~~~~~~~~~~

All SCXML state machine models processable by DataGenerator require three elements::

    <scxml xmlns="http://www.w3.org/2005/07/scxml" version="1.0" initial="start">

        <state id="start">
            <transition event="FIRST_REAL_STATE" target="FIRST_REAL_STATE"/>
        </state>

        <state id="end">
        </state>

    </scxml>

1. The SCXML tag, including the name space, version number of 1.0 and an initial state

2. A start state the corresponds to the given initial state. We find it easiest to make the start state always be "start" and to have this filler start state transition to the real start state.

3. An end state, which must have the id of "end"

Tags
----

SCXML has many tags that correspond to statements that control or define the state machine. Four are used by DataGenerator: state, onentry, assign, and transition.

State
~~~~~

The state statement simply defines a state in the state machine::

    <state id="SOME_STATE">
    </state>

The id of each state needs to be unique.

Onentry
~~~~~~~

The onentry statement defines what happens when a state is reached during the search over the state machine. Onentry statements must be nested inside a state statement with only one onentry statement per state statement.::

    <state id="SOME_STATE">
        <onentry>
            <assign ... />
            <assign ... />
            <assign ... />
        </onentry>
    </state>

Assign
~~~~~~

The assign statement makes the search assign a value to a state machine variable. Assign statements must be nested within an onentry statement::

    <onentry>
        <assign name="VARIABLE_NAME" expr="VARIABLE_ASSIGNMENT" />
    </onentry>

'expr' can either be 

1. a text literal::

    <assign name="var_Date" expr="9/9/2014" />

2. a text literal that represents a macro::

    <assign name="var_Date" expr="currentDate" />

3. a set assignment::

    <assign name="var_Word" expr="set:{Lorem,Ipsum,Doler,Sit,Amet}" />

4. or a variable's name that represents a macro::

    <assign name="var_Name" expr="${var_Date}" />

Text literals and text literals that represent a macro are the same as far as the state machine model and the search logic is concerned. The difference is handled by the DataConsumer and DataTransformers that perform post processing of search results. Set assignments assign to the variable one of the entries listed between the curly braces. During the search over the state machine, the search will search over all possible values of the set assignment, treating each as a separate path. Variable's name that represents a macro assigns the value of the variable that is specified in the macro. Like other macros, the DataConsumer and DataTransformers post process the search results. 

A state can have more than one assign statement, and all variables will be assigned. A state can also have more than one assign statement that uses a set, and when there are multiple set assignments every combination of possible assignments will be searched over.

Assign statements implicitly define the set of variables in a state machine. Any variable name used in an assign statement is a variable of the state machine, and any variable name not used is not. A variable can be assigned by more than one assign statement; later assign statements overwrite prior assign statements during the search. If a variable is defined by an assign statement, but the state with that assign statement is not reached during a search path, the variable will have the default value of "" in the search result.

Transition
~~~~~~~~~~

Transitions connect states to one another and inform how the search over the state machine progresses. Transition statements require a target state and event. The state must be in the model and the event must be the same as the target state. A condition for the transition is optional.::

    <state id="SOME_STATE">
        <onentry>
            <assign ... />
            <assign ... />
            <assign ... />
        </onentry>

        <transition state="ASSIGN_NAME" event="ASSIGN_NAME" />
        <transition state="ASSIGN_DATE" event="ASSIGN_DATE"
            cond="${var_1 != var_2 || var_Word == 'Lorem'}" />
    </state>

Searches over the state machine will follow transitions from state to state, with the end state marking a completed search path. Transitions will only be followed if their condition evaluates to true or if the transition has no condition. The search will search over every valid transition, just as it searches over every combination of variable assignments when using set assignments.

Putting it All Together
-----------------------

The basic example page has a complete example of an SCXML state machine usable with DataGenerator.