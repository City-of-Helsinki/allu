#!/bin/bash
if [ -z $1 ]; then
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_database.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_elasticsearch.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass -vvv local_backend.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_frontend.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_sftpserver.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_reporting_database.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_etl.yml --vault-password-file ~/allu_keys/vault_secret
else
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_database.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_elasticsearch.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass --extra-vars="var_branch=$1" local_backend.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_frontend.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_sftpserver.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_reporting_database.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i dev.inventory --private-key=$HOME/allu_keys/allu_id_rsa --ask-become-pass local_etl.yml --vault-password-file ~/allu_keys/vault_secret
fi
