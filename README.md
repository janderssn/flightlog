# flightlog
Tool for scraping info from no.flightlog.org  

Main goal is to be able to get info about activity on take off.

Pilots are logging their activity in no.flightlog.org. There is no API for 
reaching det storage behind the web pages so scraping i the only  way to go.

# PostgreSQL Setup

```
sudo -u postgres psql
postgres=# create database flightlog;
postgres=# create user flightlog with encrypted password 'flightlog';
postgres=# grant all privileges on database flightlog to flightlog;
```

```
java -Dspring.profiles.active=postgresql -jar flightlog.jar
```

# Swagger UI

http://localhost:8080/swagger-ui.html