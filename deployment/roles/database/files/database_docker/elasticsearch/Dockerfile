FROM docker.elastic.co/elasticsearch/elasticsearch:7.17.9

USER root

RUN mv /usr/share/elasticsearch /tmp/silly_aufs_fix && \
    mv /tmp/silly_aufs_fix /usr/share/elasticsearch && \
    chown -R elasticsearch:elasticsearch /usr/share/elasticsearch

USER elasticsearch