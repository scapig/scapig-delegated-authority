## scapig-delegated-authority

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
docker tag scapig-delegated-authority scapig/scapig-delegated-authority:VERSION
docker login
docker push scapig/scapig-delegated-authority:VERSION
``

## Running
``
docker run -p9013:9013 -d scapig/scapig-delegated-authority:VERSION
``
