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
RUN mkdir -p /home/allu/supervision-api

######################################################################################
# Expose ports
######################################################################################

# supervision-api
EXPOSE 9050

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /home/allu/supervision-api


######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

COPY ./src/main/resources/application.properties /home/allu/supervision-api/supervision-api.properties
COPY ./src/main/resources/logback.xml /home/allu/supervision-api/logback.xml
# JAR files
COPY ./target/supervision-api-1.0-SNAPSHOT.jar /home/allu/supervision-api.jar
ENTRYPOINT ["java", "-Xmx256m", "-Dservice.home=/home/allu/supervision-api", \
 "-jar","/home/allu/supervision-api.jar", "--spring.config.location=file:/home/allu/supervision-api/supervision-api.properties"]
