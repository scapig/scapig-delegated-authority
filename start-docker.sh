#!/bin/sh
SCRIPT=$(find . -type f -name scapig-delegated-authority)
rm -f scapig-delegated-authority*/RUNNING_PID
exec $SCRIPT -Dhttp.port=7030 -J-Xms128M -J-Xmx512m
