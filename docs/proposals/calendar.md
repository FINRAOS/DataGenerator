#Trade Calendar

This proposal was originally outlined in issue, [Requirements for Trade Calendar #254] (https://github.com/FINRAOS/DataGenerator/issues/254)

##Motivation

Financial applications need to differentiate business days from non-business days. Non-business days are weekends and holidays.

Additionally, financial applications may also need to know market open and close times on daily basis because market open and close times are not consistent for all business days. Markets may close early on the day before a holiday. Occassionally, a one-off external event may trigger an early close, for example, the flash crash on 2015-05-06. 

Business day arithmetic is somewhat different from typical calendar arithmetic. The current business day is conventionally referred to as "T". The next business day is "T+1" and the preceding business day is "T-1". Simlarily, 3 business days in the future would be "T+3" and four business days in the past would be "T-4". 

####Example:

Consider the Christmas week of 2015. Christmas falls on Friday, 2015-12-25:

The markets close at 3:00 on Thursday, 2015-12-24 and are closed on Friday, 2015-12-25. 

 If T is Monday, 2015-12-21, then  
  1. T-1 is Friday, 2015-12-18  
  1. T+1 is Tuesday, 2015-12-22  
  1. T+4 is Monday, 2015-12-28

To effectively test business applications, it is necessary to generate dates for:
 1. business days
  1. ranges of business days between a start date and an end date
  2. ranges of business days from a start date and extending forward N buisness days
  3. ranges of business days from an end date and extending back N business days
 2. non-business days
 3. business days between market open and close times
 4. business days outside of market open and close times
 5. equivalence classes:
  1. positive and negative dates and/or times for each range of dates and/or times
  2. positive and negative boundary dates and/or times for each range of dates and/or times

##Proposal

This feature can be implemented in stages:

######Stage 1: 
Using the Java Calendar class, implement the distinction between weekdays and weekends. Because the Java Calendar class returns day of week, we can leverage it to implement three equivalence classes:
 1. all days within a range of dates
 1. weekdays within a range of dates
 1. weekends within a range of dates
 
######Stage 2: 
Introduce a user supplied date list which lists dates as YYYY-mm-dd. This list can be used to specify inclusions or omissions. Using the list and stage 1, we should be able to support the following equivalence classes:
 1. all days within a range of dates, including only the dates in the list. 
 2. all days within a range of dates, excluding the dates in the list
 1. all weekdays within a range of dates, including only the dates in the list. 
 2. all weekdays within a range of dates, excluding the dates in the list
 1. all weekends within a range of dates, including only the dates in the list. 
 2. all weekends within a range of dates, excluding the dates in the list

######Stage 3: 
Introduce a trade calendar table with columns
 1. date (YYYY-mm-dd)
 1. weekday (boolean)
 1. holiday (boolean)
 1. openTime (hh:mm:ss)
 1. closeTime (hh:mm:ss)
 
Using the trade calendar, we should be able to support the following equvalence classes:
 1. all days/weekdays/weekends within a range of dates
 2. all days/weekdays/weekends within a range of dates excluding holidays
 3. all holidays within a range of dates excluding weekdays/weekends
 4. other combinations of the above
 5. all date/times between openTime and closeTime
 
Using the trade calendar, we should be able to support the following equivalence classes:
 1. all days and times within a range of dates
 2. all business days and times within a range of dates 
  * business day: weekday && ! holiday && time >= openTime && time <= closeTime)
 
For all stages, we will need positve and negative generators. Both should take into account scenarios such as:
 1. If a range of weekdays is specified, a weekends within the range are negative cases.
 2. Is a range of business days is specified
  3. weekends and holidays within the range are negative test cases
  4. times before openTime and after closeTime are negative test cases
 

