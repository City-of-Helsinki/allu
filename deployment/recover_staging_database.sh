#!/bin/bash
if [ -z $1 ]; then
  ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa database_recover.yml --vault-password-file ~/allu_keys/vault_secret
else
  ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="recovery_target_time=$1" database_recover.yml --vault-password-file ~/allu_keys/vault_secret
fi
