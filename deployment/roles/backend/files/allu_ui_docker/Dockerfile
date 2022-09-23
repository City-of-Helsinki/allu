FROM eclipse-temurin:8-jre-alpine

ARG CUSTOM_UID
ARG CUSTOM_GID

RUN apk update \
    && apk add wget tzdata \
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
    adduser allu --uid $CUSTOM_UID --ingroup allu --disabled-password --gecos ''

######################################################################################
# Create directories
######################################################################################

# general setup
RUN mkdir -p /home/allu/.ssh

# project setup
RUN mkdir -p /home/allu/allu-ui-service

######################################################################################
# Expose ports
######################################################################################

# allu-ui-service
EXPOSE 9000

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /servicehome

######################################################################################
# Symbolic links
######################################################################################

# Logging directory (assumes that /servicehome/allu-ui-service/logs directory exist)
RUN ln -s /servicehome/logs/allu-ui-service /home/allu/allu-ui-service/logs;
# Service runner scripts (assumes that /servicehome/allu-ui-service directory exist and contain expected scripts)
RUN ln -s /servicehome/services/allu-ui-service/run_allu-ui-service.sh /home/allu/allu-ui-service/run_allu-ui-service.sh;
# Service configuration files (assumes that /servicehome/allu-ui-service directory exist and contain expected configuration files)
RUN ln -s /servicehome/services/allu-ui-service/included-logback.xml /home/allu/allu-ui-service/included-logback.xml;
RUN ln -s /servicehome/services/allu-ui-service/allu-ui-service.properties /home/allu/allu-ui-service/allu-ui-service.properties;

######################################################################################
# Copy files to image
######################################################################################

COPY known_hosts /home/allu/.ssh/known_hosts
RUN chmod 777 /home/allu/.ssh/known_hosts

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1
ARG SERVICE_MEMORY=256m
ENV SERVICE_MEMORY=$SERVICE_MEMORY

# JAR files
RUN wget https://allu.jenkins.vincit.io/artifactory/libs-snapshot-local/fi/hel/allu/allu-ui-service/1.0-SNAPSHOT/allu-ui-service-1.0-SNAPSHOT.jar --output-document /home/allu/allu-ui-service/allu-ui-service.jar;
ENTRYPOINT exec java -Xmx$SERVICE_MEMORY -Dservice.home=/home/allu/allu-ui-service \
    -jar /home/allu/allu-ui-service/allu-ui-service.jar --spring.config.additional-location=file:/home/allu/allu-ui-service/allu-ui-service.properties
