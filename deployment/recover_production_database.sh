#!/bin/bash
if [ -z $1 ]; then
  ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa database_recover.yml --vault-password-file ~/allu_keys/vault_secret
else
  target_time_valid=$(TZ='UTC' date -d $1 +%Z)
  if [ "$target_time_valid" = "UTC" ]; then
    ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="recovery_target_time=$1" database_recover.yml --vault-password-file ~/allu_keys/vault_secret
  else
    echo "Bad recovery_target_time format. 'date' command could not parse date time."
    exit 1
  fi
fi
