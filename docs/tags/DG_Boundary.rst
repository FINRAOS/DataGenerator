Boundary Values
===============


Using DataGenerator, a user can generate boundary values to test edge case scenarios for ranges of Hive or ANSI SQL* data types.

A range is defined by it lower bound and upper bound. If the lower bound is included in the range, the range is said to be left-closed. If the lower bound is not included in the range, the range is said to be left-open. Similarly, depending whether the upper bound is included in the range, the range is said to be right-closed or right-open.

**Hive ``tinyInt``, ``smallInt``, ``int``, and ``bigInt`` data types**
|
Positive test cases includes
 1. the lower bound (if the lower bound is closed)
 2. the upper bound (if the upper bound is closed)
 3. a mid point
 4. the lower bound + 1
 5. the upper bound - 1 
 6. ``0`` if it is in the range
 7. ``null`` if ``nullable=true``. 
|
Negative cases include
 1. the lower bound (if the lower bound is open)
 2. the upper bound (if the upper bound is open)
 3. the lower bound - 1
 4. the upper bound + 1
 5. ``null`` if ``nullable=false``.

If the lower bound or upper bound is not specified, the it is closed at the minimum or maximum value for that data type.

The Hive ``Decimal`` data type not only has a lower and upper bound, but also a precision and scale, where precision is the total number of digits and scale is the number of digits after the decimal point. Consequently, in addition to the lower and upper bound test cases described for the integer types, there are test cases for precision and scale. For the Hive ``Decimal`` data type, a positive test will also include a decimal with ``precision-scale`` digits to the left of the decimal point and ``scale`` digits to the right, ``0`` digits to the left of the decimal point and ``scale`` digits to the right, ``precision-scale`` digits to the left of the decimal point and ``0`` digits to the right, precision-scale digits and decimal point, and ``null`` if ``nullable=true``. A negative test will also include a decimal with ``precision-scale+1`` digits to the left of the decimal point and ``scale-1`` digits to the right, ``precision-scale-1`` digits to the left of the decimal point and ``scale+1`` digits to the right, and ``null`` if ``nullable=false``.

For the Hive ``Varchar`` data type, a positive test will include a varchar of length ``length``, a varchar of length random length
less than ``length``, and null if ``nullable=true``. A negative test case will include a varchar of length 
``length`` + 1 and null if ``nullable=false``.


**Examples**

Below, we create boundary conditions for a Hive TinyInt field. With min set to -10 and max set to 100,
the values that will be generated for this field are -10, -9, 0, 45, 99, 100, and null. ::

<dg:positiveBoundHiveTinyInt name="SIZE" min="-10" max="100" nullable="true"/>

Now, we modify the previous tag to make it a negative case and set nullable to false. The values that will be generated for this field are -11, 101, and null. ::

<dg:negativeBoundHiveTinyInt name="SIZE" min="-10" max="100" nullable="false"/>

The example above can be applied to the Hive smallInt, int, and bigInt data types in the same manner.

Below, we create boundary conditions for a Hive Decimal field. With length set to 18,8.
The values that will be generated for this field are a decimal containing ``min``, a decimal just inside ``max`` with length 18,8, a decimal with value that is the mid point of the min and max, and ``null``. ::

<dg:negativeBoundHiveDecimal name="SIZE" length="18,8" min="100" max="1000" nullable="true"/>

For positive test cases:

|  If ``min`` is specified, the ``min`` value will be included in the data.
|  If ``max`` is specified, a value with ``max - 1`` with ``scale`` number of trailing digits will be included in the data
|  if ``minLen`` is specified, a value with length ``minLen`` will be included in the data
|  If ``maxLen`` is specified, a value with length ``maxLen`` will be included in the data
|

Note: Length can be specified with only precision, or with both precision and scale. If no length is specified, the default precision is 10, and the default scale is 0. If ``maxLen`` is greater than ``length``, it will be ignored, and ``length`` will be used instead.

For negative test cases:

|  A non decimal value will be included in the data
|  If ``min`` is specified, ``min - 1`` will be included in the data.
|  If ``max`` is specified, a value with ``max + 1`` with ``scale + 1`` number of trailing digits will be included in the data
|  if ``minLen`` is specified, a value with length ``minLen - 1`` will be included in the data
|  If ``maxLen`` is specified, a value with length ``maxLen + 1`` will be included in the data
|


Below, we create boundary conditions for a hive Varchar type. We specify ``length`` of 10 and ``minLen`` of 5.
The values that will be generated for a positive case are a varchar of length 5, a varchar of length 10, a varchar of length 7, and null. ::

<dg:positiveBoundHiveVarchar name="varField" length="10" minLen="5" nullable="true"/>

Had this been a negative case, DG would generate a varchar of length 4, and a varchar of length 11.


Below, we create boundary conditions for the hive ``date`` type. We specify the ``earliest`` and ``latest`` dates in the range. For a positive case, DG will generate a date equal to ``earliest``, a date one day after ``earliest`` a date equal to ``latest`` and a date one day before ``latest``. If we do not specify the ``earliest``, the default is 1970-01-01. If we do not specify ``latest``, the default is the current date. For negative cases, DG will generate a date one day before ``earliest``, and one day after ``latest``.

<dg:positiveBoundHiveDate name="TDate" earliest="2014-01-01" latest="2014-12-31 nullable="true"/>
|

----

Hive Types
^^^^^^^

**Date** 

| ``<dg:positiveBoundHiveDate name="TDATE" nullable="true"/>`` 
| ``<dg:negativeBoundHiveDate name="TDATE" nullable="true"/>``
|  
| Required Parameters: ``name``
| Optional Parameters: ``earliest``, ``latest``, ``nullable``
|

**Decimal** 

| ``<dg:positiveBoundHiveDecimal name="SIZE" length="18,8" nullable="true"/>`` 
| ``<dg:negativeBoundHiveDecimal name="SIZE" length="18,8" nullable="true"/>``
|  
| Required Parameters: ``name``
| Optional Parameters: ``min``, ``max``, ``nullable``, ``length``, ``minLen``, ``maxLen``, ``nullable``
|
  
**Varchar** 

| ``<dg:positiveBoundHiveVarchar name="SIZE" length="18" minLen=10 maxLen=18 nullable="true"/>`` 
| ``<dg:negativeBoundHiveVarchar name="SIZE" length="18" minLen=10 maxLen=18 nullable="true"/>``
|  
| Required Parameters: ``name``
| Optional Parameters: ``nullable``, ``length``, ``minLen``, ``maxLen``
|
  
**TinyInt** 

| ``<dg:positiveBoundHiveTinyInt name="SIZE" min="-10" max="100" nullable="false"/>``
| ``<dg:negativeBoundHiveTinyInt name="SIZE" min="-10" max="100" nullable="false"/>``
|  
| Required Parameters: ``name``
| Optional Parameters: ``min``, ``max``, ``nullable``
|

**SmallInt** 

| ``<dg:positiveBoundHiveSmallInt name="SIZE" min="-10" max="100" nullable="false"/>``
| ``<dg:negativeBoundHiveSmallInt name="SIZE" min="-10" max="100" nullable="false"/>``
|
| Required Parameters: ``name``
| Optional Parameters: ``min``, ``max``, ``nullable``
|

**Int** 

| ``<dg:positiveBoundHiveInt name="SIZE" min="-10" max="100" nullable="false"/>``
| ``<dg:negativeBoundHiveInt name="SIZE" min="-10" max="100" nullable="false"/>``
|
| Required Parameters: ``name``
| Optional Parameters: ``min``, ``max``, ``nullable``
|

**BigInt** 

| ``<dg:positiveBoundHiveBigInt name="SIZE" min="-10" max="100" nullable="false"/>``
| ``<dg:negativeBoundHiveBigInt name="SIZE" min="-10" max="100" nullable="false"/>``
| 
| Required Parameters: ``name``
| Optional Parameters: ``min``, ``max``, ``nullable``

----


.. [*] unimplemented
