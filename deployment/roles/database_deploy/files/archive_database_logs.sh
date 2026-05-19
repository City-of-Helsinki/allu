#!/bin/bash

set -e

LOG_FILE=/etc/cron.d/allu_archive-database-logs.log
LOGS_DIR=/srv/data/allu/database/data/log
BACKUP_DIR=/srv/backup/allu/database
BACKUP_FILE=${BACKUP_DIR}/postgresql-logs-$(date +%Y-%m-%d).tar.gz

# Check free space of data drive in %
FREE_SPACE=`df "$LOGS_DIR" | tail -n 1 | awk '{print $5}' | tr -d %`

# Exit if there is enough free space
if [ "$FREE_SPACE" -lt "89" ]; then
    echo "$(date +%Y-%m-%d) - Free disk space of $LOGS_DIR: $FREE_SPACE%, exiting." >> $LOG_FILE
    exit 0
fi

echo "Free disk space of $LOGS_DIR: $FREE_SPACE%" >> $LOG_FILE
logger "Start archiving database logs: $(date)"

# Find log files older than 30 days, archive and compress them to backup disk. Remove archived files
find "$LOGS_DIR" \( -name 'postgresql-*.log' -mtime +30 -type f \) -print0 | tar --remove-files -I pigz -cf $BACKUP_FILE --null -T - >> $LOG_FILE 2>&1

if [ $? -eq 0 ]; then
    logger "Allu: Database logs archived succesfully to $BACKUP_FILE"
    exit 0
else
    echo "$(date +%Y-%m-%d) - Error archiving database logs."
    exit 1