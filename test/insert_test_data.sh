#!/usr/bin/env bash

function fail() {
  echo "$*"
  exit 1
}

# Compare the submitted return value with curl's failure code
function curl_failed() {
  if [ "$1" -ne "0" ]; then
    echo "Request failed (exit code=$1). Current request:"
    echo $CURRENT_REQUEST
    return 0;
  fi
  return 1;
}

# Run given command three times
function try_thrice() {
  for n in 1 2 3 ; do
    eval "$*"
    if curl_failed $?; then
      if [ $n -lt 3 ] ; then
         echo Sleeping 10 seconds before retry..
         sleep 10
      fi
    else
      return 0
    fi
  done
  fail "Three attempts, three failures. Giving up."
}

function login() {
  CURRENT_REQUEST="curl -f --silent -X POST http://$TARGET_HOST/api/auth/login"
  AUTH_KEY=$( $CURRENT_REQUEST )
}

function insert_data() {
  echo Inserting data from $1
  CURRENT_REQUEST="curl -f --silent -X POST --header \"Content-Type: application/json\" --header \"Authorization: Bearer $AUTH_KEY\" --data @$1 http://$TARGET_HOST/api/applications &> /dev/null"
  eval $CURRENT_REQUEST
}

if [ "$1" = "" ] ; then
  fail Usage: $0 fronted_host
fi

TARGET_HOST=$1

try_thrice login
try_thrice insert_data "data/hernesaari.json"
try_thrice insert_data "data/tervasaari.json"
