FROM eclipse-temurin:17.0.5_8-jre-focal

ARG CUSTOM_UID=1214
ARG CUSTOM_GID=7898

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
VOLUME /home/allu/pdf-service

######################################################################################
# Copy files to image
######################################################################################

# wkhtmltopdf
COPY wkhtmltox-0.12.3_linux-generic-amd64.tar.xz /tmp/wkhtmltox.tar.xz
RUN tar xf /tmp/wkhtmltox.tar.xz -C /usr/local && rm /tmp/wkhtmltox.tar.xz

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

COPY ./src/main/resources/application.properties /home/allu/pdf-service/pdf-service.properties
COPY ./src/main/resources/application-docker.properties /home/allu/pdf-service/application-docker.properties
COPY ./src/main/resources/logback.xml /home/allu/pdf-service/logback.xml
COPY ./stylesheets/ /home/allu/pdf-service/stylesheets/
# JAR files
COPY ./target/pdf-service-1.2-SNAPSHOT.jar /home/allu/pdf-service/pdf-service.jar
ENTRYPOINT ["java", "-Xmx256m", "-Dservice.home=/home/allu/pdf-service", \
 "-jar", "/home/allu/pdf-service/pdf-service.jar", \
 "--spring.profiles.active=DEV,docker", \
 "--spring.config.additional-location=file:/home/allu/pdf-service/"]