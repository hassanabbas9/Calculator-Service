# Calculator App #

## Build & Run ##

```sh
The service is deployed on heroku at this url:
https://fast-coast-65076.herokuapp.com/

## How to run from the browser ##
url: https://fast-coast-65076.herokuapp.com/calculus?query=Mis0KjEwLzMNCg==

## Explaination ##
query paramter takes the base64 encoded string expression for instance considering
query=2+4*10/3
which would become 'Mis0KjEwLzMNCg==' after the encoding is applied
so the final url will become https://fast-coast-65076.herokuapp.com/calculus?query=Mis0KjEwLzMNCg==
which will return the results in json format

## Run locally ##
$ cd Calculator_App
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
