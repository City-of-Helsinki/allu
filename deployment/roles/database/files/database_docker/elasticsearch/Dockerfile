FROM docker.elastic.co/elasticsearch/elasticsearch:5.4.1

USER root

COPY ./lib/log4j-core-2.16.0.jar ./lib
COPY ./lib/log4j-1.2-api-2.16.0.jar ./lib
COPY ./lib/log4j-api-2.16.0.jar ./lib

RUN mv /usr/share/elasticsearch /tmp/silly_aufs_fix && \
    mv /tmp/silly_aufs_fix /usr/share/elasticsearch && \
    chown -R elasticsearch:elasticsearch /usr/share/elasticsearch

RUN  rm /usr/share/elasticsearch/lib/log4j-api-2.8.2.jar && rm /usr/share/elasticsearch/lib/log4j-1.2-api-2.8.2.jar && rm  /usr/share/elasticsearch/lib/log4j-core-2.8.2.jar
RUN elasticsearch-plugin remove x-pack
USER elasticsearch
