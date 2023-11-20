#!/bin/bash
if [ -z $1 ]; then
  echo "Usage: $0 <limit_hosts>"
  echo "For example: $0 dbservers"
else
  ansible-playbook -i production.inventory imageprep.yml --limit $1 --private-key $HOME/allu_keys/allu_id_rsa --vault-password-file ~/allu_keys/vault_secret --extra-vars "root_public_key=$HOME/allu_keys/allu_id_rsa.pub allu_public_key=$HOME/allu_keys/allu_id_rsa.pub deployment_user=jenkins"
fi
