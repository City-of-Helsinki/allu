#!/bin/bash

ARCHIVE_DIR=/srv/backup/allu/database/wal_archive

# find the latest .backup file in wal_archive
LATEST_BACKUP=`ls -c ${ARCHIVE_DIR}/*.backup 2> /dev/null|head -n 1`

# check that we found something
if [ "$LATEST_BACKUP" == "" ]
then
  logger Allu: wal_cleanup.sh found no .backup files, aborting archive cleanup
  exit 1
fi

# we need the file name without path
LATEST_BACKUP=`basename $LATEST_BACKUP`

logger Allu: Cleaning wal archives up to $LATEST_BACKUP

docker exec allu-database pg_archivecleanup $ARCHIVE_DIR $LATEST_BACKUP
