![MenuMate](frontend/public/static/noscript/logo.png)

## Description

MenuMate is a web application developed with Spring, Jersey and React. It is a unified order handler designed to facilitate seamless interactions between restaurants and users. This application streamlines the entire order process, making it more efficient and user-friendly.

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
| menumateapp.user+snape@gmail.com    | MenuMate moderator                                                     |

Since all of these go to the same Gmail account, we have set up filters to work as multiple inboxes. Emails sent to these addresses will automatically be directed to the respective labels within the Gmail account. To access that account, simply use the following credentials:

* Email: `menumateapp.user@gmail.com`
* Password: `***REMOVED***`

## Final Remarks

This project was done in an academic environment, as part of the curriculum of Web Applications Project from Instituto Tecnológico de Buenos Aires (ITBA)

The project was carried out by:

* Alejo Flores Lucey
* Ivan Chayer
* Nehuén Gabriel Llanos
* Thomas Mizrahi

### Final Exam Correction

* Incluyeron un catálogo de la API en la raíz, pero el mismo está incompleto, por ejemplo, no incluye ningún query param. Del mismo modo no se incluye ninguna entidad que no viva en la raíz.
* Aunque cambiar los filtros de búsqueda impacta en la URL del browser, el navegar hacía atrás / adelante por la historia no tiene efecto. No actualiza los filtros ni los resultados de búsqueda.

Nota final: 9
