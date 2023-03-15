########################################################################################################
# Modified ElasticSearch Docker image to support change of uid and gid running the ElasticSearch process
########################################################################################################
FROM docker.elastic.co/elasticsearch/elasticsearch:7.0.0
ARG CUSTOM_UID
ARG CUSTOM_GID
USER root
# The workaround below could maybe be avoided by using multi-stage builds: https://docs.docker.com/engine/userguide/eng-image/multistage-build/#use-multi-stage-builds
# i.e. using COPY --from=0 /usr/share/elasticsearch /usr/share/elasticsearch (or something similar)
RUN mv /usr/share/elasticsearch /tmp/silly_aufs_fix && \
    mv /tmp/silly_aufs_fix /usr/share/elasticsearch && \
    groupmod -g $CUSTOM_UID elasticsearch && \
    usermod -u $CUSTOM_GID elasticsearch && \
    chown -R elasticsearch:elasticsearch /usr/share/elasticsearch

RUN  rm /usr/share/elasticsearch/lib/log4j-api-2.11.1.jar \
    && rm /usr/share/elasticsearch/lib/log4j-1.2-api-2.11.1.jar \
    && rm  /usr/share/elasticsearch/lib/log4j-core-2.11.1.jar

COPY ./lib/log4j-core-2.17.1.jar /usr/share/elasticsearch/lib
COPY ./lib/log4j-1.2-api-2.17.1.jar /usr/share/elasticsearch/lib
COPY ./lib/log4j-api-2.17.1.jar /usr/share/elasticsearch/lib

# Remove x-pack since it requires a license
USER elasticsearch