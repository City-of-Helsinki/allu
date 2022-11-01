#!/bin/bash

## Fix stylesheet directories
mkdir -p /home/allu/allu-backend/services/pdf-service
cp -r ../deployment/roles/backend/files/pdf-stylesheets /home/allu/allu-backend/services/pdf-service/stylesheets
chown -R 1214:7898 /home/allu/allu-backend
chmod -R 744 /home/allu/allu-backend/*
chmod 644 /home/allu/allu-backend/services/pdf-service/stylesheets/*
