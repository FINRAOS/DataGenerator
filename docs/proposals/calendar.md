#Trade Calendar

This proposal was originally outlined in issue, [Requirements for Trade Calendar #254] (https://github.com/FINRAOS/DataGenerator/issues/254)

##Motivation

Financial applications need to differentiate business days from non-business days. Non-business days are weekends and holidays.

Additionally, financial applications may also need to know market open and close times on daily basis because market open and close times are not consistent for all business days. Markets may close early on the day before a holiday. Occassinally, a one-off external event may trigger an early close, for example, the flash crash on 2015-05-06. 

Business day arithmetic is somewhat different from typical calendar arithmetic. The current business day is conventionally referred to as "T". The next business day is "T+1" and the preceding business day is "T-1". Simlarily, 3 business days in the future would be "T+3" and four business days in the past would be "T-4". 

####Example:

Consider the Christmas week of 2015. Christmas falls on Friday, 2015-12-25:

The markets close at 3:00 on Thrusday, 2015-12-25 and are closed on Friday, 2015-12-25. 

 If T is Monday, 2015-12-21, then  
  1. T-1 is Friday, 2015-12-18  
  1. T+1 is Tuesday, 2015-12-22  
  1. T+4 is Monday, 2015-12-28

##Proposal

This feature can be implemented in stages:

Stage 1: Using the Java Calendar class, implement the distinction between weedkdays and weekends. Because the Java Calendar class returns day of week, we can leverage it to implement three equivalence classes:
 1. all days - within a range of dates
 1. weekdays - within a range of dates
 1. weekends - within a range of dates
 
Stage 2: Introduce a user supplied date list which lists dates as YYYY-mm-dd. This list can be used to specify omissions or inclusion.

Stage 3: Introduce a trade calendar table with columns
 1. date (YYYY-mm-dd)
 1. weekday (boolean)
 1. holiday (boolean)
 1. openTime (hh:mm:ss)
 1. closeTime (hh:mm:ss)
 

