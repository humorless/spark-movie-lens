# spark-movie-lens

generated using Luminus version "2.9.11.90"

## initalization of Database

```
lein migratus migrate
```

## Prerequisites

1. You will need [Leiningen][1] 2.0 or above installed.
2. You will need to instll postgresql and pre-create user and database.
3. Install libraries needed for spark.

```
sudo apt-get install libgfortran3
```

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run

Point chrome to http://localhost:3000

### Check spark stages

Point chrome to http://localhost:4040/stages/

## Deployment

After uberjar built.

```
java -Ddatabase-url="jdbc:postgresql://localhost/intowow_dev?user=intowow&password=qwerty" -jar target/uberjar/intowow.jar
```

## License

Copyright © 2017 Laurence Chen

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
