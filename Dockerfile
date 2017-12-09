FROM openjdk:8

COPY target/universal/scapig-delegated-authority-*.tgz .
COPY start-docker.sh .
RUN chmod +x start-docker.sh
RUN tar xvf scapig-delegated-authority-*.tgz

EXPOSE 7030