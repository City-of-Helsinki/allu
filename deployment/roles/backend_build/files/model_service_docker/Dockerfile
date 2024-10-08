FROM eclipse-temurin:17.0.5_8-jre-focal

ARG CUSTOM_UID
ARG CUSTOM_GID

# Use fi.archive.ubuntu.com as our CI has restricted outgoing HTTP (:80) access
# (http://archive.ubuntu.com and http://security.ubuntu.com (has no https alt) were blocked at the time of writing this)
RUN echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal main restricted universe multiverse' > /etc/apt/sources.list \
 && echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal-updates main restricted universe multiverse' >> /etc/apt/sources.list \
 && echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal-backports main restricted universe multiverse' >> /etc/apt/sources.list

RUN apt-get -y update \
    && apt-get -y install wget tzdata \
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
RUN mkdir -p /home/allu/model-service

######################################################################################
# Expose ports
######################################################################################

#model-service
EXPOSE 9010

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /servicehome

######################################################################################
# Symbolic links
######################################################################################

# Logging directories (assumes that /servicehome/model-service/logs directories exist)
RUN ln -s /servicehome/logs/model-service /home/allu/model-service/logs
# Service configuration files (assumes that /servicehome/model-service directories exist and contain expected configuration files)
RUN ln -s /servicehome/services/model-service/included-logback.xml /home/allu/model-service/included-logback.xml
RUN ln -s /servicehome/services/model-service/model-service.properties /home/allu/model-service/model-service.properties

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
RUN echo $CACHEBUST
ARG SERVICE_MEMORY=256m
ENV SERVICE_MEMORY=$SERVICE_MEMORY

# JAR files
RUN wget http://10.235.144.143/artifactory/libs-snapshot-local/fi/hel/allu/model-service/1.2-SNAPSHOT/model-service-1.2-SNAPSHOT.jar --output-document /home/allu/model-service/model-service.jar
ENTRYPOINT exec java -Xmx$SERVICE_MEMORY -Dservice.home=/home/allu/model-service \
    -jar /home/allu/model-service/model-service.jar --spring.config.additional-location=file:/home/allu/model-service/model-service.properties
