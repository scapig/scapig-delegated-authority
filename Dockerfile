FROM openjdk:8

COPY target/universal/tapi-delegated-authority-*.tgz .
COPY start-docker.sh .
RUN chmod +x start-docker.sh
RUN tar xvf tapi-delegated-authority-*.tgz

EXPOSE 7030