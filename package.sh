#!/bin/sh
sbt universal:package-zip-tarball
docker build -t scapig-delegated-authority .
docker tag scapig-delegated-authority scapig/scapig-delegated-authority
docker push scapig/scapig-delegated-authority
