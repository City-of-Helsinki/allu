FROM docker.elastic.co/elasticsearch/elasticsearch:6.0.0

USER root

RUN  rm /usr/share/elasticsearch/lib/log4j-api-2.9.1.jar \
    && rm /usr/share/elasticsearch/lib/log4j-1.2-api-2.9.1.jar \
    && rm  /usr/share/elasticsearch/lib/log4j-core-2.9.1.jar


COPY ./lib/log4j-core-2.17.1.jar /usr/share/elasticsearch/lib
COPY ./lib/log4j-1.2-api-2.17.1.jar /usr/share/elasticsearch/lib
COPY ./lib/log4j-api-2.17.1.jar /usr/share/elasticsearch/lib

RUN mv /usr/share/elasticsearch /tmp/silly_aufs_fix && \
    mv /tmp/silly_aufs_fix /usr/share/elasticsearch && \
    chown -R elasticsearch:elasticsearch /usr/share/elasticsearch

RUN elasticsearch-plugin remove x-pack --purge
RUN sed -i.bak -e /xpack.license.self_generated.type/d config/elasticsearch.yml

USER elasticsearch
