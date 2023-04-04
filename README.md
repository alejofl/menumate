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
It should download, set-up and run the environment. This will take some time. The container will run on port 5432 of the host and exposes the service on port 5432, which is the default port used by PostgreSQL. Port 5432 is the access point from the host to establish a connection with the PostgreSQL container.

Then, compile the Maven project to download the dependencies:
```sh
mvn compile
```

Create a `application.properties` file to add some basic properties. You can copy the `application.properties.sample` file from the repo. 

Finally, run the application with Tomcat

To shut down docker, just send a sigterm (Ctrl+C) or use `docker-compose down`. If you want to delete the database volume, run `docker-compose down -v`