#!/bin/sh

APP_NAME=TweetLooper
BASE_DIR=/var/tweetlooper
JAR_FILE=tweetlooper-1.0.0-SNAPSHOT.jar
PID=$BASE_DIR/pid/tl.pid

case "$1" in
  "start" )
    if [ ! -f ${PID} ]; then
      echo "Starting $APP_NAME"
      cd $BASE_DIR
      java -jar $JAR_FILE &
      echo $! > $PID
    else
      echo "$APP_NAME is Running."
    fi
    ;;
  "stop" )
    echo "Stopping $APP_NAME"
    kill -s 9 `cat $PID`
    RETVAL=$?
    if test $RETVAL -eq 0 ; then
        rm -f $PID
    fi
    ;;
  *)
    echo "Usage: tweetlooper.sh {start|stop}"
    exit 1
esac
