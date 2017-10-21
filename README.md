# intowow

generated using Luminus version "2.9.11.90"

## initalization of Database

```
lein repl
(start)
(ns intowow.db.core)
(init-ratings!) ;; write ua.base into DB
(init-movies!)  ;; write u.item into DB
(init-users!)   ;; write a dummy user into DB
```

## Debug: invoke DB web console
```
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

## License

Copyright © 2017 FIXME
