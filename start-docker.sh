#!/bin/sh
SCRIPT=$(find . -type f -name scapig-delegated-authority)
rm -f scapig-delegated-authority*/RUNNING_PID
exec $SCRIPT -Dhttp.port=9013 $JAVA_OPTS -J-Xms16M -J-Xmx64m
