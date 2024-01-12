## Code Demo- Henry Meds
### Assumptions
* There is an authorization library that exists.
* Appointment confirmation to the provider is a future feature
* Only one Time Zone is supported due to leveraging localization instead of ms from Epoch
* Front end passes through client's time. - This also makes manual testing easier.
### Things I'd Like to improve, given time
* Testing. Only covered unit testing that caught a couple issues.
* Authorization of requests
* Implement metrics, including logging and time to completion into Splunk or another like data sink
* Sonar implementation and pipeline scanning, to catch errors
* Spotless, for style/formatting enforcement
* Allowing for the same hours every week, and integration with software that tracks time off to hide days that the 
provider has time off scheduled
* Time Zone management and implementation.
* Management for paperwork time, lunch breaks, etc.
* Better error management and language surrounding it.
* Date and Time management could be much better
### Trains of thought that could use more exploration
* How to better format the database schema.
* Appointment generation- I went with what came to mind, but it's a little clunky.
* Appointment reservation system would remove the availability of time slots on the appointment lookup.
* Convert all time references to milliseconds from epoch. That would make timing much easier, but it works.