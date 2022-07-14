FROM openjdk:8-jre-slim-bullseye

ARG CUSTOM_UID=1214
ARG CUSTOM_GID=7898

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
RUN mkdir -p /home/allu/pdf-service
RUN mkdir -p /home/allu/scheduler-service
RUN mkdir -p /home/allu/external-service
RUN mkdir -p /home/allu/scheduler-service/sap
RUN mkdir -p /home/allu/supervision-api

######################################################################################
# Expose ports
######################################################################################

# pdf-service
EXPOSE 9030
# external-service
EXPOSE 9040
# supervision-api
EXPOSE 9050

######################################################################################
# Supervisord
######################################################################################

# supervisord
RUN pip3 install --upgrade pip
RUN pip3 install supervisor
RUN pip3 install git+https://github.com/coderanger/supervisor-stdout
COPY backend.supervisord.conf /etc/supervisord.conf
CMD /usr/local/bin/supervisord --configuration=/etc/supervisord.conf

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /servicehome

######################################################################################
# Symbolic links
######################################################################################

RUN for service in `find /home/allu -mindepth 1 -maxdepth 1 -type d -exec basename \{} \;`; do ln -s /servicehome/logs/"${service}" "/home/allu/${service}"/logs; done
# Service runner scripts (assumes that /home/allu/<servicename> directories exist and contain expected scripts)
COPY backend.run_service.sh /home/allu/pdf-service/run_pdf-service.sh
COPY backend.run_service.sh /home/allu/scheduler-service/run_scheduler-service.sh
COPY backend.run_service.sh /home/allu/external-service/run_external-service.sh
COPY backend.run_service.sh /home/allu/supervision-api/run_supervision-api.sh
# Service configuration files (assumes that /home/allu/<servicename> directories exist and contain expected configuration files)
COPY pdf-service/src/main/resources/logback.xml /home/allu/pdf-service/logback.xml
COPY scheduler-service/src/main/resources/logback.xml /home/allu/scheduler-service/logback.xml
COPY external-service/src/main/resources/logback.xml /home/allu/external-service/logback.xml
COPY supervision-api/src/main/resources/logback.xml /home/allu/supervision-api/logback.xml

COPY pdf-service/src/main/resources/application.properties /home/allu/pdf-service/pdf-service.properties
COPY scheduler-service/src/main/resources/application.properties /home/allu/scheduler-service/scheduler-service.properties
COPY external-service/src/main/resources/application.properties /home/allu/external-service/external-service.properties
COPY supervision-api/src/main/resources/application.properties /home/allu/supervision-api/supervision-api.properties
# Properties for running services on Docker locally
COPY backend.properties /home/allu/pdf-service/application-docker.properties
COPY backend.properties /home/allu/scheduler-service/application-docker.properties
COPY backend.properties /home/allu/external-service/application-docker.properties
COPY backend.properties /home/allu/supervision-api/application-docker.properties

COPY pdf-service/target/pdf-service-1.0-SNAPSHOT.jar /home/allu/pdf-service/pdf-service.jar
COPY scheduler-service/target/scheduler-service-1.0-SNAPSHOT.jar /home/allu/scheduler-service/scheduler-service.jar
COPY external-service/target/external-service-1.0-SNAPSHOT.jar /home/allu/external-service/external-service.jar
COPY supervision-api/target/supervision-api-1.0-SNAPSHOT.jar /home/allu/supervision-api/supervision-api.jar

RUN for service in `find /home/allu -mindepth 1 -maxdepth 1 -type d -exec basename \{} \;`; do chmod -R 755 /home/allu; done

# Stylesheets for pdf-service
RUN ln -s /servicehome/services/pdf-service/stylesheets /home/allu/pdf-service/stylesheets

# Directories for SAP customer files
RUN ln -s /servicehome/services/scheduler-service/sap/customer-archive /home/allu/scheduler-service/sap/customer-archive
RUN ln -s /servicehome/services/scheduler-service/sap/customer-failed /home/allu/scheduler-service/sap/customer-failed
RUN ln -s /servicehome/services/scheduler-service/sap/customer-process /home/allu/scheduler-service/sap/customer-process
# Directory for invoices
RUN ln -s /servicehome/services/scheduler-service/sap/invoice-archive /home/allu/scheduler-service/sap/invoice-archive

######################################################################################
# Copy files to image
######################################################################################

# wkhtmltopdf
COPY wkhtmltox-0.12.3_linux-generic-amd64.tar.xz /tmp/wkhtmltox.tar.xz
RUN tar xf /tmp/wkhtmltox.tar.xz -C /usr/local && rm /tmp/wkhtmltox.tar.xz
