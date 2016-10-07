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

# Run given command a few times
function try_repeat() {
  for n in 1 2 3 4 5 6 7 8; do
    eval "$*"
    if curl_failed $?; then
      if [ $n -lt 8 ] ; then
         echo Sleeping 15 seconds before retry..
         sleep 15
      fi
    else
      return 0
    fi
  done
  fail "Three attempts, three failures. Giving up."
}

function login() {
  CURRENT_REQUEST="curl -f --silent -X POST --header \"Content-Type: application/json\" --data '{\"userName\": \"$1\"}' http://$TARGET_HOST/api/auth/login"
  AUTH_KEY=$( eval $CURRENT_REQUEST )
}

function create_user() {
  echo Creating user from $1
  CURRENT_REQUEST="curl -f --silent -X POST --header \"Content-Type: application/json\" --header \"Authorization: Bearer $AUTH_KEY\" --data @$1 http://$TARGET_HOST/api/users &> /dev/null"
  eval $CURRENT_REQUEST
}

function insert_data() {
  echo Inserting data from $1
  CURRENT_REQUEST="curl -f --silent -X POST --header \"Content-Type: application/json\" --header \"Authorization: Bearer $AUTH_KEY\" --data @$1 http://$TARGET_HOST/api/applications &> /dev/null"
  eval $CURRENT_REQUEST
}

function search_and_ignore() {
  echo Sending a search request..
  CURRENT_REQUEST="curl -f --silent -X POST --header \"Content-Type: application/json\" --header \"Authorization: Bearer $AUTH_KEY\" --data '{\"queryParameters\":[{\"fieldName\":\"handler\",\"fieldValue\":\"TestHandler\"}]}' http://$TARGET_HOST/api/applications/search &> /dev/null"
  eval $CURRENT_REQUEST
}

if [ "$1" = "" ] ; then
  fail Usage: $0 fronted_host
fi

TARGET_HOST=$1

try_repeat login admin
try_repeat create_user data/kasittelija.json
try_repeat create_user data/paattaja.json
try_repeat login kasittelija
# Make sure search service is up:
try_repeat search_and_ignore
try_repeat insert_data "data/hernesaari.json"
try_repeat insert_data "data/tervasaari.json"
