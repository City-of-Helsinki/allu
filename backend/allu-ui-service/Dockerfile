FROM eclipse-temurin:8u345-b01-jre-focal

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
VOLUME /home/allu/allu-ui-service

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

COPY ./src/main/resources/application.properties /home/allu/allu-ui-service/allu-ui-service.properties
COPY ./src/main/resources/application-docker.properties /home/allu/allu-ui-service/application-docker.properties
COPY ./src/main/resources/logback.xml /home/allu/allu-ui-service/logback.xml
# JAR files
COPY ./target/allu-ui-service-1.0-SNAPSHOT.jar /home/allu/allu-ui-service/allu-ui-service.jar
ENTRYPOINT ["java", "-Xmx256m", "-Dservice.home=/home/allu/allu-ui-service", \
 "-jar","/home/allu/allu-ui-service/allu-ui-service.jar", \
 "--spring.profiles.active=DEV,docker", \
 "--spring.config.additional-location=file:/home/allu/allu-ui-service/"]
