Boundary Values
===============


Using DataGenerator, a user can generate boundary values to test edge case scenarios for ranges of Hive or ANSI SQL data types. A range is defined by its minumum and maximum values.

**Hive ``tinyInt``, ``smallInt``, ``int``, and ``bigInt`` data types**
 
Positive test cases include

 #. the minimum
 #. the maximum
 #. a random value between the minimum and maximum
 #. the minimum + 1
 #. the maximum - 1 
 #. ``0`` if it is in the range
 #. ``null`` if ``nullable=true`` 
 
Negative cases include

 #. the minimum - 1
 #. the maximum + 1
 #. ``null`` if ``nullable=false``

If the minimum or maximum is not specified, then minimum or maximum for that data type is assumed.

**Hive ``Decimal`` data type**

The Hive ``Decimal`` data type not only has a minimum and maximum, but also a precision and scale, where precision is the total number of digits and scale is the number of digits to the right the decimal point. 

Positive test cases include

 #. the minimum
 #. the minimum + 1/(10^scale)
 #. the maximum
 #. the maximum - 1/(10^scale)
 #. a random value between the minimum and maximum with ``precision-scale`` digits to the left of the decimal point and ``scale`` digits to the right
 #. a random value between the minimum and maximum with ``0`` digits to the left of the decimal point and ``scale`` digits to the right
 #. a random value between the minimum and maximum with ``precision-scale`` digits to the left of the decimal point and ``0`` digits to the right
 #. a random value between the minimum and maximum with ``precision-scale`` digits and no decimal point
 #. ``null`` if ``nullable=true`` 
 
Negative test cases include

 #. the minimum - 1/(10^scale)
 #. the maximum + 1/(10^scale)
 #. a random value between the minimum and maximum with ``precision-scale+1`` digits to the left of the decimal point and ``scale-1`` digits to the right
 #. a random value between the minimum and maximum with ``precision-scale-1`` digits to the left of the decimal point and ``scale+1`` digits to the right
 #. ``null`` if ``nullable=false`` 
 
If the minimum or maximum is not specified, then minimum or maximum for Decimal is assumed. Siimilarly, if no precision is specified, the maximum for Decimal is assumed. If no scale is specified, 0 is assumed.

**Hive ``Varchar`` data type**

Note: The set of allowed  characters must be defined.

Positive test cases include

 #. a ``Varchar`` of length ``length``
 #. a ``Varchar`` of a random length less than ``length``
 #. ``null`` if ``nullable=true`` 
 
Negative test cases include
 
 #. a Varchar of length ``length`` + 1
 #. null if ``nullable=false``
 #. a Varchar of random length less than ``length`` containing an invalid character

The ``length`` must be specified.

**Examples**

Hive TinyInt:

<dg:positiveBoundHiveTinyInt name="SIZE" min="-10" max="100" nullable="true"/> generates {-10, -9, 0 45, 99, null}.

<dg:negativeBoundHiveTinyInt name="SIZE" min="-10" max="100" nullable="false"/> generates {-11, 101, null}.

Hive Decimal

<dg:positiveBoundHiveDecimal name="SIZE" precision="7" scale="3" min="100" max="1000" nullable="true"/> generates {100, 100.001 455.555, 999.999, 1000, null}

<dg:negativeBoundHiveDecimal name="SIZE" precision="7" scale="3" min="100" max="1000" nullable="false"/> generates {99.999, 1000.001, 4555.55, 455.5555, null}

For positive test cases:


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
