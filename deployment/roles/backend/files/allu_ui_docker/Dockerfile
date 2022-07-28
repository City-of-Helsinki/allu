FROM openjdk:8-jre-slim-bullseye

ARG CUSTOM_UID
ARG CUSTOM_GID

RUN apt-get -y  update \
    && apt-get -y install wget xz-utils git python3-pip tzdata autossh libxrender1 libfontconfig libxtst6 \
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

# project setup
RUN mkdir -p /home/allu/allu-ui-service

######################################################################################
# Expose ports
######################################################################################

# allu-ui-service
EXPOSE 9000

######################################################################################
# Supervisord
######################################################################################

# supervisord
RUN pip3 install --upgrade pip
RUN pip3 install supervisor
RUN pip3 install git+https://github.com/coderanger/supervisor-stdout
COPY supervisord.conf /etc/supervisord.conf
CMD /usr/local/bin/supervisord --configuration=/etc/supervisord.conf

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
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

# JAR files
RUN wget https://allu.jenkins.vincit.io/artifactory/libs-snapshot-local/fi/hel/allu/allu-ui-service/1.0-SNAPSHOT/allu-ui-service-1.0-SNAPSHOT.jar --output-document /home/allu/allu-ui-service/allu-ui-service.jar;
