#!/bin/bash

LOG_FILE=/etc/cron.d/allu_database-backup.log
BACKUP_DIR=/srv/backup/allu/database/dumps
LATEST=$BACKUP_DIR/latest
PREVIOUS=$BACKUP_DIR/previous

echo "Start backup: $(date)" >> $LOG_FILE

# Rotate existing backup to free space for the new one
if [ -d "$LATEST" ]
then
  rm -rf "$PREVIOUS"
  mv "$LATEST" "$PREVIOUS"
fi

# -Ft: tar format, -z: gzip compression, -X stream: stream WAL files
docker exec allu-database pg_basebackup -h localhost -U postgres -D $LATEST -Ft -z -X stream >> $LOG_FILE 2>&1
if [ $? -eq 0 ]
then
  echo "Backup successful, deleting previous dump" >> $LOG_FILE
  rm -rf "$PREVIOUS"
else
  echo "Backup failed, retaining previous dump" >> $LOG_FILE
  rm -rf "$LATEST"
fi

echo "End backup: $(date)" >> $LOG_FILE
