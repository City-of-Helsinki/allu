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
VOLUME /home/allu




######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

# JAR files
RUN wget https://allu.jenkins.vincit.io/artifactory/libs-snapshot-local/fi/hel/allu/allu-ui-service/1.0-SNAPSHOT/allu-ui-service-1.0-SNAPSHOT.jar  \
    --output-document /home/allu-ui-service.jar
#RUN chmod 777 /home/allu/allu-ui-service/allu-ui-service.jar
ENTRYPOINT ["java","-jar","/home/allu-ui-service.jar"]
