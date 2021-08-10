#! /bin/bash
mvn clean install
mkdir target/dependency
cd target/dependency || exit
jar -xf ../*.jar
cd ../../