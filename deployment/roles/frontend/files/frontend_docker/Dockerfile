FROM ubuntu:16.04

ARG CUSTOM_UID
ARG CUSTOM_GID

RUN apt-get update \
    && apt-get install -y tzdata apache2 cron libapache2-mod-wsgi python-pip python-imaging python-yaml python-dev libproj9 build-essential libjpeg-dev zlib1g-dev libfreetype6-dev \
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
    adduser allu --uid $CUSTOM_UID --gid $CUSTOM_GID --disabled-password --gecos ''

######################################################################################
# Create directories
######################################################################################

RUN mkdir -p /home/allu/mapproxy
RUN mkdir -p /home/allu/apache

######################################################################################
# Expose ports
######################################################################################

# Apache
EXPOSE 80
EXPOSE 443

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /mapproxy
VOLUME /etc/apache2/sites-available
VOLUME /var/www/html
VOLUME /etc/ssl/allu

######################################################################################
# Apache
######################################################################################

COPY fqdn.conf ssl-params.conf /etc/apache2/conf-available/

COPY apache_start.sh /home/allu/apache
RUN chmod ugo+x /home/allu/apache/apache_start.sh
CMD /home/allu/apache/apache_start.sh

RUN a2enmod proxy
RUN a2enmod proxy_http
RUN a2enmod rewrite
RUN a2enmod headers
RUN a2enmod wsgi
RUN a2enconf fqdn ssl-params
RUN a2enmod ssl

RUN a2dissite 000-default
# Disable automated Apache start on system start-up (Apache is executed in foreground, not as daemon)
RUN update-rc.d apache2 remove
RUN systemctl disable apache2

######################################################################################
# Python pip (mapproxy installation)
######################################################################################
RUN pip install Pillow
RUN pip install MapProxy==1.10.0
RUN pip install requests==2.18.4

######################################################################################
# Symbolic links
######################################################################################

# Apache configuration
RUN ln -s /etc/apache2/sites-available/allufront.conf /etc/apache2/sites-enabled/allufront.conf
# Mapproxy logging
RUN ln -s /mapproxy/logs /home/allu/mapproxy/logs
# Mapproxy cache
RUN ln -s /mapproxy/cache_data /home/allu/mapproxy/cache_data
# Mapproxy configuration
RUN ln -s /mapproxy/configuration /home/allu/mapproxy/configuration
# Mapproxy Python initialization
RUN ln -s /mapproxy/python /home/allu/mapproxy/python

######################################################################################
# Cron jobs
######################################################################################

COPY root_crontab /tmp/root_crontab
RUN crontab /tmp/root_crontab
RUN rm /tmp/root_crontab

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1
