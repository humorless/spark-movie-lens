# intowow

generated using Luminus version "2.9.11.90"


## invoke DB web console
```
java -cp h2*.jar org.h2.tools.Server -webAllowOthers
```

## initalization of Database

```
(intowow.db.core/write-ratings) ;; write ua.base into DB
(intowow.db.core/write-movies)  ;; write u.item into DB
```

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run 

## License

Copyright © 2017 FIXME
