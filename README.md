## scapig-delegated-authority

This is the microservice responsible for storing and retrieving the user tokens created by the Scapig API Gateway (http://www.scapig.com)

## Building
``
sbt clean test it:test component:test
``

## Packaging
``
sbt universal:package-zip-tarball
docker build -t scapig-delegated-authority .
``

## Publishing
``
docker tag scapig-delegated-authority scapig/scapig-delegated-authority
docker login
docker push scapig/scapig-delegated-authority
``

## Running
``
docker run -p9013:9013 -d scapig/scapig-delegated-authority
``
