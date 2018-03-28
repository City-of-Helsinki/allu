FROM postgres:9.6

ARG POSTGRES_PWD

ENV POSTGRES_PASSWORD=$POSTGRES_PWD

RUN apt-get update \
  && apt-get install -y postgresql-9.6-postgis-2.3 postgresql-9.6-postgis-2.3-scripts --no-install-recommends \
  && rm -rf /var/lib/apt/lists/*

RUN localedef -i fi_FI -c -f UTF-8 -A /usr/share/locale/locale.alias fi_FI.UTF-8
ENV LANG fi_FI.utf8


######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

COPY pg_hba.conf /pg_hba.conf
RUN chmod ugo+rw /pg_hba.conf
COPY copy_pg_hba.sh /docker-entrypoint-initdb.d/copy_pg_hba.sh
RUN chmod ugo+x /docker-entrypoint-initdb.d/copy_pg_hba.sh
