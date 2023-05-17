![MenuMate](webapp/src/main/webapp/static/pictures/logo.png)

| Student               | ID    |
|-----------------------|-------|
| Alejo Flores Lucey    | 62622 |
| Iván Chayer           | 61630 |
| Nehuén Gabriel Llanos | 62511 |
| Thomas Mizrahi        | 60154 |

## Description

MenuMate is a web application developed with Spring MVC is a unified order handler designed to facilitate seamless interactions between restaurants and users. This application streamlines the entire order process, making it more efficient and user-friendly.

The webapp can be accessed [here](http://pawserver.it.itba.edu.ar/paw-2023a-01/)


## Users in prod
We have six users set up in our production database, using the following emails:

* `menumateapp.user+draco@gmail.com`

* `menumateapp.user+hagrid@gmail.com`

* `menumateapp.user+harry@gmail.com`

* `menumateapp.user+hermione@gmail.com`

* `menumateapp.user+ron@gmail.com`

* `menumateapp.user+snape@gmail.com`

The password for all these users is the same: `***REMOVED***`

Within our app, these users have the following permissions:

| User                                | Restaurant Role                                                        |
|-------------------------------------|------------------------------------------------------------------------|
| menumateapp.user+draco@gmail.com    | Owner at Empanada Delicia - Order Handler at La Paloma and Honest Food |
| menumateapp.user+hagrid@gmail.com   | Order Handler at Honest Food                                           |
| menumateapp.user+harry@gmail.com    | Owner at Solonia Parrilla                                              |
| menumateapp.user+hermione@gmail.com | Admin at Solomia Parrilla                                              |
| menumateapp.user+ron@gmail.com      | Owner at Honest Food - Admin at Solomia Parrilla                       |
| menumateapp.user+snape@gmail.com    | -                                                                      |

Since all of these go to the same Gmail account, we have set up filters to work as multiple inboxes. Emails sent to these addresses will automatically be directed to the respective labels within the Gmail account. To access that account, simply use the following credentials:

* Email: `menumateapp.user@gmail.com`
* Password: `***REMOVED***`

