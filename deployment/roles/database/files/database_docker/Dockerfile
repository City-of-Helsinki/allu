FROM postgres:9.6

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

# Copy the database initialization script to be run on first DB startup
COPY init.sql /docker-entrypoint-initdb.d/10-init.sql
