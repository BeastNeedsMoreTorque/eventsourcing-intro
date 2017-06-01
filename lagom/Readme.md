# Lagom Persistence

## Running the example

### Read Side Database

The read side is provided in a MySQL database. So before you start the application, you need to start a MySQL server on your localhost that listens to port 3306. The application by default uses the user 'root' with no passwort, but this can be changed in application.conf.

Please create a database "league" and initialize it with the file "misc/league.sql" (it will create the necessary tables).

### Starting the application

Once the database is up and running, just execute

```
$> cd league
$> sbt
> runAll
```

### Using the application

It's just HTTP/JSON for now, no web interface. You can submit requests with httpie or curl. You'll find some example JSON data in "misc/leaguedata.json"

