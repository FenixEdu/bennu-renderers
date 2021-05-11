#!/usr/bin/env bash
mvn --batch-mode -Dtag=$1 release:prepare -DreleaseVersion=$1 -DdevelopmentVersion=DEV-SNAPSHOT