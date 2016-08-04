# heroku-openid

A Scala Play app to be used to test our implementation of openid connect

## Run Locally

Make sure you have Play and sbt installed.  Also, install the [Heroku Toolbelt](https://toolbelt.heroku.com/).

```sh
$ git clone https://github.com/philip-o/heroku-openid.git
$ cd heroku-openid
$ sbt compile stage
$ heroku local web
```

## Live URL

The application is currently running at https://morning-chamber-29407.herokuapp.com/