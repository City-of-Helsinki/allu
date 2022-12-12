#!/bin/bash
java -Xmx256m -Dservice.home=/home/allu/scheduler-service -jar /home/allu/scheduler-service/scheduler-service.jar --spring.profiles.active=DEV,docker --spring.config.additional-location=file:/home/allu/scheduler-service/
