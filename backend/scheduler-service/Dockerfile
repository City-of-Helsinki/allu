FROM eclipse-temurin:17.0.5_8-jre-focal

ARG CUSTOM_UID=1214
ARG CUSTOM_GID=7898

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

COPY run_scheduler-service.sh /home/allu/scheduler-service/run_scheduler-service.sh
COPY src/main/resources/logback.xml /home/allu/scheduler-service/logback.xml
COPY src/main/resources/application.properties /home/allu/scheduler-service/scheduler-service.properties
COPY src/main/resources/application-docker.properties /home/allu/scheduler-service/application-docker.properties
COPY target/scheduler-service-1.2-SNAPSHOT.jar /home/allu/scheduler-service/scheduler-service.jar

RUN chmod -R 755 /home/allu

# Directories for SAP customer files
RUN ln -s /servicehome/services/scheduler-service/sap/customer-archive /home/allu/scheduler-service/sap/customer-archive
RUN ln -s /servicehome/services/scheduler-service/sap/customer-failed /home/allu/scheduler-service/sap/customer-failed
RUN ln -s /servicehome/services/scheduler-service/sap/customer-process /home/allu/scheduler-service/sap/customer-process
# Directory for invoices
RUN ln -s /servicehome/services/scheduler-service/sap/invoice-archive /home/allu/scheduler-service/sap/invoice-archive