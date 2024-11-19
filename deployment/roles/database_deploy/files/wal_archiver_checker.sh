#!/bin/bash

set -e

LOG_DIR=/srv/data/allu/database/data/log

EXPECTED_LOG_PATTERN="The failed archive command was: test ! -f /srv/backup/allu/database/wal_archive/[0-9A-F]\{24\} && cp pg_wal/[0-9A-F]\{24\} /srv/backup/allu/database/wal_archive/[0-9A-F]\{24\}"

# Find the latest postgres log file

LATEST_LOG=`ls -c $LOG_DIR/postgres*.log|head -n 1`

# grep for failed archive commands

LOG_MSG=`grep 'failed archive command' $LATEST_LOG|tail -n 1`

# If found, check if the failure was in the last five minutes
# (otherwise it is probably an old error we already dealt with)

if [ "$LOG_MSG" == "" ]
then
  logger "Allu: No wal archiving errors found in $LATEST_LOG, exiting"
  exit 0
fi

LOG_TIME=`echo $LOG_MSG|grep -o '^....-..-.. ..:..:..'`
LOG_TIMESTAMP=`date "+%s" -d "$LOG_TIME"`
FIVE_MINUTES_AGO=`date "+%s" -d "-5 mins"`

if [ "$LOG_TIMESTAMP" -lt "$FIVE_MINUTES_AGO" ]
then
  logger Allu: Detected wal archiving problem that was more than five minutes ago \(${LOG_TIME}\), ignoring and exiting
  exit 0
fi

# If the problem is at most five minutes old, check if failure is of the expected type
# (otherwise we don't know how to deal with it)

if ! echo "$LOG_MSG"|grep -q "$EXPECTED_LOG_PATTERN"
then
  logger Allu: Detected wal archiving problem whose error message is not of the expected pattern, don\'t know what to do so aborting \(message was: $LOG_MSG\)
  exit 1
fi

# If it was of the expected pattern, extract the failed file from message

FAILED_FILE=`echo $LOG_MSG|grep -o "test ! -f [^ ]*"|cut -d ' ' -f 4`

# Check the size of the failed file

FILE_SIZE=`stat --printf="%s" "$FAILED_FILE"`

# If size is zero just remove the file,
# else rename the file with extension .allufailsaife to be safe
# (we very much don't want to remove real wal files by mistake)

if [ "$FILE_SIZE" == "0" ]
then
  rm $FAILED_FILE
  logger Allu: wal archiving probled detected, removed zero-size file $FAILED_FILE
else
  mv $FAILED_FILE $FAILED_FILE.allufailsafe
  logger Allu: wal archiving problem detected, renamed non-empty file to $FAILED_FILE.allufailsafe
fi
