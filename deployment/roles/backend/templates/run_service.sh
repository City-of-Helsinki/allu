#!/bin/bash
java -jar /home/allu/{{ item }}/{{ item }}.jar --spring.config.location=file:/home/allu/{{ item }}/{{ item }}.properties &
