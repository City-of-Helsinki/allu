FROM docker.elastic.co/elasticsearch/elasticsearch:7.17.28

USER root

# log4j-core/api 2.17.1 ship with the base image already (ES 7.16.2+), so the manual
# log4j jar swap previously needed for the 6.8.0 image (bundled with vulnerable 2.11.1) is removed.

RUN mv /usr/share/elasticsearch /tmp/silly_aufs_fix && \
    mv /tmp/silly_aufs_fix /usr/share/elasticsearch && \
    chown -R elasticsearch:elasticsearch /usr/share/elasticsearch

USER elasticsearch
