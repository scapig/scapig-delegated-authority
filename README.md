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

## Running
``
docker run -p7030:7030 -i -a stdin -a stdout -a stderr scapig-delegated-authority sh start-docker.sh
``