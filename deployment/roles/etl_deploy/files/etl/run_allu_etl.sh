#!/bin/bash
java -Xmx256m -Dservice.home=/home/allu/etl -jar /home/allu/etl/allu-etl.jar --spring.config.additional-location=file:/home/allu/etl/allu-etl.properties
