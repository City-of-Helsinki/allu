#!/bin/bash
java -Xmx{{ allu_service_memory_limit[item] | default('256m') }} -Dservice.home=/home/allu/{{ item }} -jar /home/allu/{{ item }}/{{ item }}.jar --spring.config.location=file:/home/allu/{{ item }}/{{ item }}.properties &
