#!/usr/bin/env bash
if [ -z $1 ]; then
  echo "Usage: $0 <remote_user> <allu_password> <public_key_file> <target_host>"
  echo "For example: $0 root allu_secret_password \$HOME/allu_keys/allu_id_rsa.pub 10.176.127.67"
  echo "Use root as remote user for test env, for others use gofore"
else
  # NOTE that password is read from command line as group_vars are not available for imageprep.yml,
  # because it is not specific to any host (not possible to define for example test:children in inventory file)
  ALLU_PASSWORD=$( echo $2 | mkpasswd --method=SHA-512 --stdin )
  # note that the $4, is intentional. If only $4 would be used, Ansible would not recognise the parameter correctly
  ansible-playbook -i $4, imageprep.yml --ask-pass --ask-become-pass --vault-password-file ~/allu_keys/vault_secret --extra-vars "root_public_key=$3 allu_public_key=$3 system_password_allu=$ALLU_PASSWORD deployment_user=$1"
fi
