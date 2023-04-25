FROM eclipse-temurin:17.0.5_8-jre-focal

ARG CUSTOM_UID
ARG CUSTOM_GID

RUN apt-get -y update \
    && apt-get -y install wget xz-utils git python3-pip tzdata autossh coreutils \
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
RUN mkdir -p /home/allu/etl

######################################################################################
# Supervisord
######################################################################################

RUN pip3 install supervisor
RUN pip3 install git+https://github.com/coderanger/supervisor-stdout
COPY supervisord.conf /etc/supervisord.conf
CMD /usr/local/bin/supervisord --configuration=/etc/supervisord.conf


######################################################################################
# Symbolic links
######################################################################################

RUN ln -s /servicehome/logs/etl /home/allu/etl/logs

RUN ln -s /servicehome/services/etl/run_allu_etl.sh /home/allu/etl/run_allu_etl.sh
RUN ln -s /servicehome/services/etl/included-logback.xml /home/allu/etl/included-logback.xml
RUN ln -s /servicehome/services/etl/allu-etl.properties /home/allu/etl/allu-etl.properties


######################################################################################
# Copy files to image
######################################################################################



######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1

# JAR files
RUN wget https://allu.jenkins.vincit.io/artifactory/libs-snapshot-local/fi/hel/allu/allu-etl/1.2-SNAPSHOT/allu-etl-1.2-SNAPSHOT.jar --output-document /home/allu/etl/allu-etl.jar
