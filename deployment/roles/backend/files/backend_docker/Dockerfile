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
RUN mkdir -p /home/allu/.ssh

# project setup
RUN mkdir -p /home/allu/allu-ui-service
RUN mkdir -p /home/allu/model-service
RUN mkdir -p /home/allu/pdf-service
RUN mkdir -p /home/allu/search-service
RUN mkdir -p /home/allu/scheduler-service
RUN mkdir -p /home/allu/external-service
RUN mkdir -p /home/allu/scheduler-service/sap
RUN mkdir -p /home/allu/supervision-api


######################################################################################
# Expose ports
######################################################################################

# allu-ui-service
EXPOSE 9000
# model-service
EXPOSE 9010
# allu-ui-service
EXPOSE 9020
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

# Logging directories (assumes that /servicehome/<servicename>/logs directories exist)
RUN for service in `find /home/allu -mindepth 1 -maxdepth 1 -type d -exec basename \{} \;`; do ln -s /servicehome/logs/"${service}" "/home/allu/${service}"/logs; done
# Service runner scripts (assumes that /servicehome/<servicename> directories exist and contain expected scripts)
RUN for service in `find /home/allu -mindepth 1 -maxdepth 1 -type d -exec basename \{} \;`; do ln -s /servicehome/services/"${service}/run_${service}.sh" "/home/allu/${service}"/run_${service}.sh; done
# Service configuration files (assumes that /servicehome/<servicename> directories exist and contain expected configuration files)
RUN for service in `find /home/allu -mindepth 1 -maxdepth 1 -type d -exec basename \{} \;`; do ln -s /servicehome/services/"${service}/included-logback.xml" "/home/allu/${service}"/included-logback.xml; done
RUN for service in `find /home/allu -mindepth 1 -maxdepth 1 -type d -exec basename \{} \;`; do ln -s /servicehome/services/"${service}/${service}.properties" "/home/allu/${service}"/${service}.properties; done

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

COPY known_hosts /home/allu/.ssh/known_hosts
RUN chmod 777 /home/allu/.ssh/known_hosts

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

# JAR files
RUN for service in `find /home/allu -mindepth 1 -maxdepth 1 -type d -not -path '*/\.*' -exec basename \{} \;`; \
  do wget https://allu.jenkins.vincit.io/artifactory/libs-snapshot-local/fi/hel/allu/${service}/1.0-SNAPSHOT/${service}-1.0-SNAPSHOT.jar --output-document /home/allu/${service}/${service}.jar; done
