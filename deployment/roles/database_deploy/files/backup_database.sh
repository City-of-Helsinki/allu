#!/bin/bash

LOG_FILE=/etc/cron.d/allu_database-backup.log
LATEST_DUMP=db_backup-latest.tar.gz
PREVIOUS_DUMP=db_backup-previous.tar.gz

echo "Start backup: $(date)" >> $LOG_FILE

cd /srv/backup/allu/database/dumps/
if [ -f $LATEST_DUMP ]
then
  chmod 0600 $LATEST_DUMP
  mv $LATEST_DUMP $PREVIOUS_DUMP
fi

pg_basebackup -h localhost -U postgres -D /srv/backup/allu/database/dumps/latest -X stream >> $LOG_FILE 2>&1
if [ $? -eq 0 ]
then
  # df -h >> $LOG_FILE
  tar --remove-files -zcf $LATEST_DUMP latest/ >> $LOG_FILE 2>&1
  if [ $? -eq 0 ]
  then
    echo "Backup successful, deleting previous dump" >> $LOG_FILE
    chmod 0400 $LATEST_DUMP
    rm $PREVIOUS_DUMP
  else
    echo "Backup failed, retaining previous dump" >> $LOG_FILE
    rm -rf /srv/backup/allu/database/dumps/latest
    rm -f $LATEST_DUMP
  fi
else
  echo "Backup failed, retaining previous dump" >> $LOG_FILE
  rm -rf /srv/backup/allu/database/dumps/latest
fi

echo "End backup: $(date)" >> $LOG_FILE
