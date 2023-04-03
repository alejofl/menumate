# MenuMate

## Requirements
- [Docker](https://www.docker.com/)
- [Maven](https://maven.apache.org/)
- SDK Corretto-1.8
- Tomcat 8.5


## Installation
First, run the Docker container:
```sh
docker-compose up
```
The container will run on port 5432 of the host and exposes the service on port 5432, which is the default port used by PostgreSQL. Port 15432 is the access point from the host to establish a connection with the PostgreSQL container.

Then, compile the Maven project to download the dependencies:
```sh
mvn compile
```

Finally, run the application with Tomcat
