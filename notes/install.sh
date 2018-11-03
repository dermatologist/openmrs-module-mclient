#!/bin/sh
moduleid="event"
version="2.6.0"
wget https://dl.bintray.com/openmrs/omod/$moduleid-$version.omod
mv $moduleid-$version.omod $moduleid-$version.jar
mvn install:install-file -DgroupId=org.openmrs.module -DartifactId=$moduleid -Dversion=$version -Dpackaging=jar -Dfile=$moduleid-$version.jar
