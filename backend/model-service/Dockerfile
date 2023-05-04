FROM eclipse-temurin:17.0.5_8-jre-focal

ARG CUSTOM_UID=1214
ARG CUSTOM_GID=7898

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

# model-service
EXPOSE 9010

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /home/allu/model-service

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

COPY ./src/main/resources/application.properties /home/allu/model-service/model-service.properties
COPY ./src/main/resources/application-docker.properties /home/allu/model-service/application-docker.properties
COPY ./src/main/resources/logback.xml /home/allu/model-service/logback.xml
# JAR files
COPY ./target/model-service-1.2-SNAPSHOT.jar /home/allu/model-service/model-service.jar
ENTRYPOINT ["java", "-Xmx256m", "-Dservice.home=/home/allu/model-service", \
 "-jar","/home/allu/model-service/model-service.jar", \
 "--spring.profiles.active=DEV,docker", \
 "--spring.config.additional-location=file:/home/allu/model-service/"]