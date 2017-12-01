#!/bin/sh
SCRIPT=$(find . -type f -name tapi-delegated-authority)
exec $SCRIPT -Dhttp.port=7030
