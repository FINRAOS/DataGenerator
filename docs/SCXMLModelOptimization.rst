SCXML Model Optimization
========================

Expansion Size of Individual States
-----------------------------------

Consider how a depth first search would behave on 'BULK_ASSIGN'::

    <state id="BULK_ASSIGN">
      <onentry>
        <dg:set name="var_1" set="1,2,3,4,5,6,7,8,9,10" />
        <dg:set name="var_2" set="1,2,3,4,5,6,7,8,9,10" />
        <dg:set name="var_3" set="1,2,3,4,5,6,7,8,9,10" />
        <dg:set name="var_4" set="1,2,3,4,5,6,7,8,9,10" />
        <dg:set name="var_5" set="1,2,3,4,5,6,7,8,9,10" />
      </onentry>
      ...
    </state>

A depth first search will explore the model to completion with each produced combination of variable assignments in turn. While one combination is being searched, the other combinations are stored in memory, awaiting their turn to be searched. In 'BULK_ASSIGN' the search needs to store 100000 points of future expansion. This can prove too much from a memory standpoint, depending on the size of a state's expansion. Contrast 'BULK_ASSIGN' with using five states to do the assigning::

    <state id="ASSIGN_1">
      <onentry>
        <dg:set name="var_1" set="1,2,3,4,5,6,7,8,9,10" />
      </onentry>
      <transition target="ASSIGN_2" event="ASSIGN_2" />
    </state>

    ...

    <state id="ASSIGN_5">
      <onentry>
        <dg:set name="var_5" set="1,2,3,4,5,6,7,8,9,10" />
      </onentry>
      ...
    </state>

This arrangement with multiple states is more conducive to a depth first search, as while the total number of combinations at the end is the same, the multiple assign states together only store 50 points of future expansion at any given time.

There is a time versus memory trade off benefit for splitting the assignments across multiple states, as each state transition adds more time to the search. In our experience, an individual state that expands into 1000 combinations strikes a good balance.


Placing Conditional Logic
-------------------------

The conditional check for a conditional transition between states is a slow operation compared to variable assignments. This can cause a problem when placed downstream in the model after large amounts of branching::

    <state id="BULK_ASSIGN">
      <onentry>
        <dg:set name="var_1" set="A,B,C,D,E" />
        <dg:set name="var_2" set="A,B,C,D,E" />
        <dg:set name="var_3" set="A,B,C,D,E" />
      </onentry>
      <transition event="STATE_WITH_LOGIC" event="STATE_WITH_LOGIC" />
    </state>

Any conditional logic on state transitions following 'BULK_ASSIGN' will be done 125 times as each combination produced from expanding 'BULK_ASSIGN' is explored separately. If the conditional logic depends on these variables, this is unavoidable, but if the logic is not dependent on the values assigned by 'BULK_ASSIGN' this is redundant. Where possible, one should place time expensive operations like conditional logic early in the model before large scale branching occurs. The depth first search will benefit performance wise from performing these expensive operations fewer times. 