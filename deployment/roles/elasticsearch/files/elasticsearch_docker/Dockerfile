########################################################################################################
# Modified ElasticSearch Docker image to support change of uid and gid running the ElasticSearch process
########################################################################################################
FROM docker.elastic.co/elasticsearch/elasticsearch:5.4.1
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

# Remove x-pack since it requires a license
RUN elasticsearch-plugin remove x-pack
USER elasticsearch
