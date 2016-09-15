#!/bin/bash
java -Xmx256m -Dservice.home=/home/allu/{{ item }} -jar /home/allu/{{ item }}/{{ item }}.jar --spring.config.location=file:/home/allu/{{ item }}/{{ item }}.properties &
