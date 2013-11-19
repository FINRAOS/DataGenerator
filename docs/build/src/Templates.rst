Template Specification
**********************

Summary
=======

Once all paths from the start state to the end state are figured out DataGenerator will send every combination of values to the given velocity template. The velocity template can then be used to generate a meaningful test script using the passed values.

Testing a simple Run Length Encoder
===================================

Drawing the branch specifications
---------------------------------

Run Length Encoding ( RLE ) a simple lossless data compression algorithm ( http://en.wikipedia.org/wiki/Run-length_encoding ), where repeated patterns are grouped in an ordered pair form of ( count, pattern ). For example, an input of 15 A followed by 15 B should be encoded to 15A15B. To simplify our example, our RLE encoder does not support cases where input patterns are repeated more then 255 times.

Since the theoretical number of input cases to the RLE are unlimited, we will limit the scope of our test patterns to a train of *two* sequences of bytes, which we will represent by ASCII letters. Each sequence of bytes can have symbol repetitions of 1, 5 and 16 and can be made using any of the symbols A,B or C.

From this discussion, it is obvious that we will need two stages in the branch specification. Each of those stages will contain two nodes: one responsible for setting the length, and the other is responsible for setting the symbol.

.. figure:: _static/RLEStateDiagram-1.png
    :alt: RLE State Diagram in Visio
    :figclass: align-center

The above figure shows how the branch spec was draw in Visio. The highlighted branch further shows how the shape data is used to set the variables, which will later be used in the velocity template. In this specific branch, we are setting the value of L1 to 5. All the branches set their respective variables in the same manner. For example, the S1=A branch has the shape data with "S1.setProperty" and it's value "A". The initial branch going off START has shape data of "createGroup" and value "ALL". That creates a variable group called ALL.

Writing a priliminary template
------------------------------

The velocity template file uses the passed branch specificationis to produce output. By looping over all the possible datasets, you can include your own logic that produces the output::

	#foreach ($rec in $allDatasets)
		#set ($l1 = $rec.L1.value)
		#set ($s1 = $rec.S1.value)
		#set ($l2 = $rec.L2.value)
		#set ($s2 = $rec.S2.value)
	
		Values: $l1, $s1, $l2, $s2
	#end


In the above example the script loops over the array of data in $allDataSets, which was passed by DataGenerator to the velocity script. It extracts the variables set in the state machine, and outputs them in order. Partial output of this velocity template is shown below::

        Values: 1, A, 1, B

        Values: 1, A, 1, A

        Values: 1, A, 1, C

        Values: 1, A, 5, B

        Values: 1, A, 5, A

        Values: 1, A, 5, C

        Values: 1, A, 16, B

        Values: 1, A, 16, A

        Values: 1, A, 16, C


