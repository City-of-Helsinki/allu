FROM eclipse-temurin:17.0.5_8-jre-focal

ARG CUSTOM_UID
ARG CUSTOM_GID

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
RUN mkdir -p /home/allu/external-service

######################################################################################
# Expose ports
######################################################################################

# external-service
EXPOSE 9040

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /servicehome

######################################################################################
# Symbolic links
######################################################################################

# Logging directories (assumes that /servicehome/external-service/logs directories exist)
RUN ln -s /servicehome/logs/external-service /home/allu/external-service/logs
# Service configuration files (assumes that /servicehome/external-service directories exist and contain expected configuration files)
RUN ln -s /servicehome/services/external-service/included-logback.xml /home/allu/external-service/included-logback.xml
RUN ln -s /servicehome/services/external-service/external-service.properties /home/allu/external-service/external-service.properties

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
ARG SERVICE_MEMORY=512m
ENV SERVICE_MEMORY=$SERVICE_MEMORY

# JAR files
RUN wget http://10.235.144.143/artifactory/libs-snapshot-local/fi/hel/allu/external-service/1.2-SNAPSHOT/external-service-1.2-SNAPSHOT.jar --output-document /home/allu/external-service/external-service.jar
ENTRYPOINT exec java -Xmx$SERVICE_MEMORY -Dservice.home=/home/allu/external-service \
    -jar /home/allu/external-service/external-service.jar --spring.config.additional-location=file:/home/allu/external-service/external-service.properties
