FROM debian:bullseye-20220711-slim

ARG CUSTOM_UID
ARG CUSTOM_GID

# A bit hacky: install openssl & ca-certificate from committed .deb packages
# Because http was blocked and no time to wait for firewall rules to change...
COPY ./deb/*.deb /tmp/
RUN dpkg -i /tmp/openssl*.deb \
    && dpkg -i /tmp/ca-certificates*.deb \
    && rm /tmp/*.deb

# Use https instead of http (as outgoing http was blocked in CI)
RUN echo 'deb https://deb.debian.org/debian bullseye main contrib non-free' > /etc/apt/sources.list \
 && echo 'deb https://deb.debian.org/debian bullseye-updates main contrib non-free' >> /etc/apt/sources.list \
 && echo 'deb https://security.debian.org/debian-security bullseye-security main contrib non-free' >> /etc/apt/sources.list

RUN apt-get update \
    && apt-get install -y tzdata systemctl python3-dev cron apache2 libapache2-mod-wsgi-py3 python3-pip supervisor \
    python3-yaml python3-pyproj build-essential libjpeg-dev python3-dev zlib1g-dev libfreetype6-dev \
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
RUN mkdir -p /home/allu/apache/www

######################################################################################
# Expose ports
######################################################################################

# Apache
EXPOSE 80
EXPOSE 443

######################################################################################
# Supervisord
######################################################################################
RUN pip3 install --upgrade pip
RUN pip3 install supervisor-stdout
CMD /usr/bin/supervisord --configuration=/etc/supervisord.conf

######################################################################################
# Host volumes
######################################################################################

# Service home with runner scripts, configuration etc.
VOLUME /mapproxy
VOLUME /etc/apache2/sites-available
VOLUME /etc/ssl/allu

######################################################################################
# Apache
######################################################################################

COPY fqdn.conf ssl-params.conf /etc/apache2/conf-available/

COPY apache_start.sh /home/allu/apache
RUN chmod ugo+x /home/allu/apache/apache_start.sh

RUN a2enmod proxy
RUN a2enmod proxy_http
RUN a2enmod rewrite
RUN a2enmod headers
RUN a2enmod wsgi
RUN a2enconf fqdn ssl-params
RUN a2enmod ssl

RUN a2dissite 000-default
# Disable automated Apache start on system start-up (Apache is executed in foreground, not as daemon)
RUN update-rc.d apache2 disable
RUN update-rc.d apache2 remove


######################################################################################
# Python pip (mapproxy installation)
######################################################################################

RUN pip3 install Pillow==9.2.0
RUN pip3 install MapProxy==1.14.0
RUN pip3 install requests==2.28.1
RUN pip3 install six==1.16.0
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
# Copy files to image
######################################################################################

# supervisord
COPY supervisord.conf /etc/supervisord.conf

######################################################################################
# Changes that should not be cached
######################################################################################
# DISABLE BUILD CACHING AFTER THIS LINE (use with command "docker build -t your-image --build-arg CACHEBUST=$(date +%s)")
ARG CACHEBUST=1
RUN echo $CACHEBUST

COPY --chown=www-data:www-data www/ /var/www/html/
