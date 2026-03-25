#!/bin/bash

mvn test

java -jar ./target/server-0.0.1-SNAPSHOT-jar-with-dependencies.jar -d

exit
