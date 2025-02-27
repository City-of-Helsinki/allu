FROM postgis/postgis:17-3.5-alpine

ARG POSTGRES_PWD

ENV POSTGRES_PASSWORD=$POSTGRES_PWD

RUN apk --no-cache add ca-certificates wget && \
    wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub && \
    wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.35-r0/glibc-2.35-r0.apk && \
    wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.35-r0/glibc-bin-2.35-r0.apk && \
    wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.35-r0/glibc-i18n-2.35-r0.apk && \
    apk add --force-overwrite glibc-bin-2.35-r0.apk glibc-i18n-2.35-r0.apk glibc-2.35-r0.apk

# Iterate through all locale and install it(locale.md only has fi_FI but if needed could add more)
# Note that locale -a is not available in alpine linux, use `/usr/glibc-compat/bin/locale -a` instead
COPY ./locale.md /locale.md
RUN cat locale.md | xargs -i /usr/glibc-compat/bin/localedef -i {} -f UTF-8 {}.UTF-8

# Set the lang, you can also specify it as as environment variable through docker-compose.yml
ENV LANG=fi_FI.utf8 \
    LANGUAGE=fi_FI.utf8


######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THESE TWO LINES (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1
RUN echo $CACHEBUST

#initial config files
COPY pg_hba.conf /pg_hba.conf
RUN chmod ugo+rw /pg_hba.conf
COPY copy_pg_hba.sh /docker-entrypoint-initdb.d/copy_pg_hba.sh
RUN chmod ugo+x /docker-entrypoint-initdb.d/copy_pg_hba.sh
