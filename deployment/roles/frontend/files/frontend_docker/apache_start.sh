#!/bin/bash

# remove pid file, which may prevent Apache to start during Docker container restart
rm -rf /var/run/apache2/apache2.pid
apachectl -DFOREGROUND
