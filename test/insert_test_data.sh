#!/usr/bin/env bash

function check_reply {
  if [ "$EXIT_CODE" == "22" ]; then
      echo Request failed. Current request:
      echo $CURRENT_REQUEST
      exit 1;
  fi
}

function insert_data() {
  CURRENT_REQUEST="curl -f --silent -X POST --header \"Content-Type: application/json\" --header \"Authorization: Bearer $AUTH_KEY\" --data @$1 http://$TARGET_HOST/api/applications &> /dev/null"
  eval $CURRENT_REQUEST
  EXIT_CODE=$?
  check_reply
}

TARGET_HOST=$1
CURRENT_REQUEST="curl -f --silent -X POST http://$TARGET_HOST/api/auth/login"
AUTH_KEY=$( $CURRENT_REQUEST )
EXIT_CODE=$?
check_reply

insert_data "data/tervasaari.json"
insert_data "data/hernesaari.json"
