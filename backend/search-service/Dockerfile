FROM eclipse-temurin:8-jre-alpine

ARG CUSTOM_UID=1214
ARG CUSTOM_GID=7898

RUN apk  update \
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
RUN mkdir -p /var/log/supervisor
RUN mkdir -p /home/allu/.ssh

# project setup
RUN mkdir -p /home/allu/search-service

######################################################################################
# Expose ports
######################################################################################

# search-service
EXPOSE 9020

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /home/allu/search-service


######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

COPY ./src/main/resources/application.properties /home/allu/search-service/search-service.properties
COPY ./src/main/resources/logback.xml /home/allu/search-service/logback.xml
# JAR files
COPY ./target/search-service-1.0-SNAPSHOT.jar /home/allu/search-service.jar
ENTRYPOINT ["java", "-Xmx256m", "-Dservice.home=/home/allu/search-service", \
 "-jar","/home/allu/search-service.jar", "--spring.config.location=file:/home/allu/search-service/search-service.properties"]
