#!/bin/bash
set -e
DATE=$( date '+%Y-%m-%d' )

if  [[ $1 = "testi.allu.kaupunkiymparisto.fi" ]]; then
    # Backup old certs for test 
    mv /etc/ssl/allu/certs/cert.pem /etc/ssl/allu/certs/cert.pem."$DATE" && echo "Backing up test cert.pem.."
    mv /etc/ssl/allu/certs/chain.pem /etc/ssl/allu/certs/chain.pem."$DATE" && echo "Backing up test chain.pem.."
    mv /etc/ssl/allu/certs/fullchain.pem /etc/ssl/allu/certs/fullchain.pem."$DATE" && echo "Backing up test fullchain.pem.."
    mv /etc/ssl/allu/private/privkey.pem /etc/ssl/allu/private/privkey.pem."$DATE" && echo "Backing up test privkey.pem.."

    # Install new certs for test
    cp /etc/letsencrypt/live/"$1"/cert.pem /etc/ssl/allu/certs/cert.pem && echo "Installing test cert.pem.."
    cp /etc/letsencrypt/live/"$1"/chain.pem /etc/ssl/allu/certs/chain.pem && echo "Installing test chain.pem.."
    cp /etc/letsencrypt/live/"$1"/fullchain.pem /etc/ssl/allu/certs/fullchain.pem && echo "Installing test fullchain.pem.."
    cp /etc/letsencrypt/live/"$1"/privkey.pem /etc/ssl/allu/private/privkey.pem && echo "Installing test privkey.pem.."
    exit 0
elif [[ $1 = "staging.allu.kaupunkiymparisto.fi" ]]; then
    # Backup old certs for staging 
    mv /etc/ssl/allu/certs/staging/cert.pem /etc/ssl/allu/certs/staging/cert.pem."$DATE" && echo "Backing up staging cert.pem.."
    mv /etc/ssl/allu/certs/staging/chain.pem /etc/ssl/allu/certs/staging/chain.pem."$DATE" && echo "Backing up staging chain.pem.."
    mv /etc/ssl/allu/certs/staging/fullchain.pem /etc/ssl/allu/certs/staging/fullchain.pem."$DATE" && echo "Backing up staging fullchain.pem.."
    mv /etc/ssl/allu/private/staging/privkey.pem /etc/ssl/allu/private/staging/privkey.pem"$DATE" && echo "Backing up staging privkey.pem.."

    # Install new certs for staging 
    cp /etc/letsencrypt/live/"$1"/cert.pem /etc/ssl/allu/certs/staging/cert.pem && echo "Installing staging cert.pem.."
    cp /etc/letsencrypt/live/"$1"/chain.pem /etc/ssl/allu/certs/staging/chain.pem && echo "Installing staging chain.pem.."
    cp /etc/letsencrypt/live/"$1"/fullchain.pem /etc/ssl/allu/certs/staging/fullchain.pem && echo "Installing staging fullchain.pem.."
    cp /etc/letsencrypt/live/"$1"/privkey.pem /etc/ssl/allu/private/staging/privkey.pem && echo "Installing staging privkey.pem.."
    exit 0
else
    echo "Not a valid domain name. Exiting."
    exit 1
fi