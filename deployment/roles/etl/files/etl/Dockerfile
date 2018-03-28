FROM ubuntu:16.04

ARG CUSTOM_UID
ARG CUSTOM_GID

RUN apt-get update \
    && apt-get install -y wget xz-utils supervisor python-pip openjdk-8-jre tzdata \
    && rm -rf /var/lib/apt/lists/*


######################################################################################
# Time zone
######################################################################################
ENV TZ=Europe/Helsinki
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

######################################################################################
# Add users
######################################################################################

RUN addgroup --gid $CUSTOM_GID allu && \
    adduser allu --uid $CUSTOM_UID --gid $CUSTOM_GID --disabled-password --gecos ''

######################################################################################
# Create directories
######################################################################################

# general setup
RUN mkdir -p /var/log/supervisor
RUN mkdir -p /home/allu/etl

######################################################################################
# Supervisord
######################################################################################

RUN pip install supervisor-stdout
CMD /usr/bin/supervisord --configuration=/etc/supervisord.conf


######################################################################################
# Symbolic links
######################################################################################

RUN ln -s /servicehome/logs/etl /home/allu/etl/logs

RUN ln -s /servicehome/services/etl/run_allu_etl.sh /home/allu/etl/run_allu_etl.sh
RUN ln -s /servicehome/services/etl/included-logback.xml /home/allu/etl/included-logback.xml
RUN ln -s /servicehome/services/etl/allu-etl.properties /home/allu/etl/allu-etl.properties


######################################################################################
# Copy files to image
######################################################################################

# supervisord
COPY supervisord.conf /etc/supervisord.conf

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

# JAR files
RUN wget http://185.26.51.92:8081/artifactory/libs-snapshot-local/fi/hel/allu/allu-etl/1.0-SNAPSHOT/allu-etl-1.0-SNAPSHOT.jar --output-document /home/allu/etl/allu-etl.jar
