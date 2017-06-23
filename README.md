# tributary

A tribute to fluvius workflow

# setup

### intellij

You need to enbale these options to compile:

https://immutables.github.io/apt.html#intellij-idea

# running

In project root:

```
mvn clean install
cd rest
mvn spring-boot:run
```

or to build and run an executable fat jar:

```
mvn clean install
cd rest
mvn package
java -jar target/rest-1.0-SNAPSHOT.jar
```
