# intowow

generated using Luminus version "2.9.11.90"

## initalization of Database

```
lein run migrate
lein repl
(start)
(ns intowow.db.core)
(init-ratings!) ;; write ua.base into DB
(init-movies!)  ;; write u.item into DB
(init-users!)   ;; write a dummy user into DB
```

## Database Debug: invoke DB web console
```
wget http://repo2.maven.org/maven2/com/h2database/h2/1.4.196/h2-1.4.196.jar
java -cp h2*.jar org.h2.tools.Server -webAllowOthers
```

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

Install libraries needed for spark.

```
sudo apt-get install libgfortran3
```

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run

Point chrome to http://localhost:3000

## License

Copyright © 2017 Laurence Chen

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
