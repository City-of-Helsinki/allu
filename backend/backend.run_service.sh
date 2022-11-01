#!/bin/bash

if [ -z $1 ]; then
  echo "Needs a service name: $0 <service_name>";
  exit 1;
else
  java -Xmx256m -Dservice.home=/home/allu/$1 -jar /home/allu/$1/$1.jar --spring.profiles.active=DEV,docker --spring.config.additional-location=file:/home/allu/$1/
fi
