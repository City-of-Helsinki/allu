#!/usr/bin/env bash
if [ -z $1 ]; then
  echo "Usage: $0 <remote_user> <allu_password> <target_host>"
  echo "For example: $0 root allu_secret_password 10.176.127.67"
  echo "Use root as remote user for test env, others vincit"
else
  # NOTE that password is read from command line as group_vars are not available for imageprep.yml,
  # because it is not specific to any host (not possible to define for example test:children in inventory file)
  ALLU_PASSWORD=$( echo $2 | mkpasswd --method=SHA-512 --stdin )
  # note that the $2, is intentional. If only $2 would be used, Ansible would not recognise the parameter correctly
  ansible-playbook -i $3, imageprep.yml --private-key $HOME/allu_keys/allu_id_rsa --ask-pass --ask-become-pass --vault-password-file ~/allu_keys/vault_secret --extra-vars "root_public_key=$HOME/allu_keys/allu_id_rsa.pub allu_public_key=$HOME/allu_keys/allu_id_rsa.pub system_password_allu=$ALLU_PASSWORD deployment_user=$1"
fi
