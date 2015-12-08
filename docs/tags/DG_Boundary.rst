Boundary Values
====

|

Overview
----

|

Using DataGenerator, a user can generate boundary values to test edge case scenarios for any Hive or ANSI SQL* data types. 

For the Hive ``tinyInt``, ``smallInt``, ``int``, and ``bigInt`` data types, a positive test case will include a value for the 
lower bound, upper bound, a mid point, values just inside the upper and lower bounds, ``0`` if it is in the range, and ``null``
if ``nullable=true``. Negative cases will include a value for the lower bound, upper bound, values just outside the upper and 
lower bounds, and ``null`` if ``nullable=false``.

For the Hive ``Decimal`` data type, a positive test case will include a decimal of length ``length``, a decimal of random length,
less than or equal to ``length``, and ``null`` if ``nullable=true``. A negative test case will include a decimal of length 
``length``, a decimal with length greater than ``length`` and ``null`` if ``nullable=false``.

For the Hive ``Varchar`` data type, a positive test case will include a varchar of length ``length``, a varchar of random length,
less than or equal to ``length``, and ``null`` if ``nullable=true``. A negative test case will include a varchar of length 
``length``, a varchar with length greater than ``length`` and ``null`` if ``nullable=false``.

|

**Examples**

Below, we create boundary conditions for a Hive TinyInt field. With min set to -10 and max set to 100,
the values that will be generated for this field are -10, -9, 0, 45, 99, 100, and null. ::

<dg:positiveBoundHiveTinyInt name="SIZE" min="-10" max="100" nullable="true"/>

Now, we slightly modify the previous tag to make it a negative case and set nullable to false. The values that will be generated for this field are -11, -10, 100, 101, and null. ::

<dg:negativeBoundHiveTinyInt name="SIZE" min="-10" max="100" nullable="false"/>

The example above can be applied to the Hive smallInt, int, and bigInt data types in the same manner.

Below, we create boundary conditions for a Hive Decimal field. With length set to 18,8,
the values that will be generated for this field are a decimal of length 18,8, a decimal of variable length,
and ``null``. ::

<dg:negativeBoundHiveDecimal name="SIZE" length="18,8" min="100.00" max="1000.00" nullable="true"/>

|

----

Hive Types
^^^^^^^

**Decimal** 

| ``<dg:positiveBoundHiveDecimal name="SIZE" length="18,8" nullable="true"/>`` 
| ``<dg:negativeBoundHiveDecimal name="SIZE" length="18,8" nullable="true"/>``
|  
| Required Parameters: ``name``, ``length``
| Optional Parameters: ``min``, ``max``, ``nullable``, ``minLen``, ``maxLen``
|
  
**Varchar** 

| ``<dg:positiveBoundHiveVarchar name="SIZE" length="18" minLen=10 maxLen=18 nullable="true"/>`` 
| ``<dg:negativeBoundHiveVarchar name="SIZE" length="18" minLen=10 maxLen=18 nullable="true"/>``
|  
| Required Parameters: ``name``, ``length``
| Optional Parameters: ``nullable``, ``minLen``, ``maxLen``
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

|
|
|

.. [*] unimplemented
