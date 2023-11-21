FROM eclipse-temurin:17.0.5_8-jre-focal

ARG CUSTOM_UID
ARG CUSTOM_GID

# Use fi.archive.ubuntu.com as our CI has restricted outgoing HTTP (:80) access
# (http://archive.ubuntu.com and http://security.ubuntu.com (has no https alt) were blocked at the time of writing this)
RUN echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal main restricted universe multiverse' > /etc/apt/sources.list \
 && echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal-updates main restricted universe multiverse' >> /etc/apt/sources.list \
 && echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal-backports main restricted universe multiverse' >> /etc/apt/sources.list

RUN apt-get -y update \
    && apt-get -y install wget xz-utils tzdata libxrender1 libfontconfig libxtst6 \
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
RUN mkdir -p /home/allu/pdf-service

######################################################################################
# Expose ports
######################################################################################

# pdf-service
EXPOSE 9030

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /servicehome

######################################################################################
# Symbolic links
######################################################################################

# Logging directories (assumes that /servicehome/pdf-service/logs directories exist)
RUN ln -s /servicehome/logs/pdf-service /home/allu/pdf-service/logs
# Service configuration files (assumes that /servicehome/pdf-service directories exist and contain expected configuration files)
RUN ln -s /servicehome/services/pdf-service/included-logback.xml /home/allu/pdf-service/included-logback.xml
RUN ln -s /servicehome/services/pdf-service/pdf-service.properties /home/allu/pdf-service/pdf-service.properties

# Stylesheets for pdf-service
RUN ln -s /servicehome/services/pdf-service/stylesheets /home/allu/pdf-service/stylesheets

######################################################################################
# Copy files to image
######################################################################################

# wkhtmltopdf
COPY wkhtmltox-0.12.3_linux-generic-amd64.tar.xz /tmp/wkhtmltox.tar.xz
RUN tar xf /tmp/wkhtmltox.tar.xz -C /usr/local && rm /tmp/wkhtmltox.tar.xz

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
RUN wget https://allu.jenkins.vincit.io/artifactory/libs-snapshot-local/fi/hel/allu/pdf-service/1.2-SNAPSHOT/pdf-service-1.2-SNAPSHOT.jar --output-document /home/allu/pdf-service/pdf-service.jar
ENTRYPOINT exec java -Xmx$SERVICE_MEMORY -Dservice.home=/home/allu/pdf-service \
    -jar /home/allu/pdf-service/pdf-service.jar --spring.config.additional-location=file:/home/allu/pdf-service/pdf-service.properties
