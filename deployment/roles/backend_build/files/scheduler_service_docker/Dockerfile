FROM eclipse-temurin:17.0.5_8-jdk-focal

ARG CUSTOM_UID
ARG CUSTOM_GID

# Use fi.archive.ubuntu.com as our CI has restricted outgoing HTTP (:80) access \
# (http://archive.ubuntu.com and http://security.ubuntu.com (has no https alt) were blocked at the time of writing this)
RUN echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal main restricted universe multiverse' > /etc/apt/sources.list \
 && echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal-updates main restricted universe multiverse' >> /etc/apt/sources.list \
 && echo 'deb http://fi.archive.ubuntu.com/ubuntu/ focal-backports main restricted universe multiverse' >> /etc/apt/sources.list

RUN apt-get -y update \
    && apt-get -y install wget git python3-pip tzdata autossh \
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
RUN mkdir -p /home/allu/scheduler-service
RUN mkdir -p /home/allu/scheduler-service/sap

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

# Logging directories (assumes that /servicehome/scheduler-service/logs directories exist)
RUN ln -s /servicehome/logs/scheduler-service /home/allu/scheduler-service/logs
# Service runner scripts (assumes that /servicehome/scheduler-service directories exist and contain expected scripts)
RUN ln -s /servicehome/services/scheduler-service/run_scheduler-service.sh /home/allu/scheduler-service/run_scheduler-service.sh
# Service configuration files (assumes that /servicehome/scheduler-service directories exist and contain expected configuration files)
RUN ln -s /servicehome/services/scheduler-service/included-logback.xml /home/allu/scheduler-service/included-logback.xml
RUN ln -s /servicehome/services/scheduler-service/scheduler-service.properties /home/allu/scheduler-service/scheduler-service.properties

# Directories for SAP customer files
RUN ln -s /servicehome/services/scheduler-service/sap/customer-archive /home/allu/scheduler-service/sap/customer-archive
RUN ln -s /servicehome/services/scheduler-service/sap/customer-failed /home/allu/scheduler-service/sap/customer-failed
RUN ln -s /servicehome/services/scheduler-service/sap/customer-process /home/allu/scheduler-service/sap/customer-process
# Directory for invoices
RUN ln -s /servicehome/services/scheduler-service/sap/invoice-archive /home/allu/scheduler-service/sap/invoice-archive

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

# JAR files
RUN wget https://allu.jenkins.vincit.io/artifactory/libs-snapshot-local/fi/hel/allu/scheduler-service/1.2-SNAPSHOT/scheduler-service-1.2-SNAPSHOT.jar --output-document /home/allu/scheduler-service/scheduler-service.jar