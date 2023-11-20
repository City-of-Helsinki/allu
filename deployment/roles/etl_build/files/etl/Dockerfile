FROM eclipse-temurin:17.0.5_8-jre-focal

ARG CUSTOM_UID
ARG CUSTOM_GID

RUN apt-get -y update \
    && apt-get -y install wget xz-utils tzdata coreutils \
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
RUN mkdir -p /home/allu/etl

######################################################################################
# Symbolic links
######################################################################################

RUN ln -s /servicehome/logs/etl /home/allu/etl/logs

RUN ln -s /servicehome/services/etl/included-logback.xml /home/allu/etl/included-logback.xml
RUN ln -s /servicehome/services/etl/allu-etl.properties /home/allu/etl/allu-etl.properties

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1
RUN echo $CACHEBUST
ARG SERVICE_MEMORY=320m
ENV SERVICE_MEMORY=$SERVICE_MEMORY

# JAR files
RUN wget http://10.235.144.143/artifactory/libs-snapshot-local/fi/hel/allu/allu-etl/1.2-SNAPSHOT/allu-etl-1.2-SNAPSHOT.jar --output-document /home/allu/etl/allu-etl.jar
ENTRYPOINT exec java -Xmx$SERVICE_MEMORY -Dservice.home=/home/allu/allu-etl \
    -jar /home/allu/etl/allu-etl.jar --spring.config.additional-location=file:/home/allu/etl/allu-etl.properties
