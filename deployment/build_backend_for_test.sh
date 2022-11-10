#!/bin/bash
if [ -z $1 ]; then
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa database_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa elasticsearch_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa backend_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa sftpserver_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa reporting_database_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa etl_build.yml --vault-password-file ~/allu_keys/vault_secret
else
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" database_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" elasticsearch_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" backend_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" sftpserver_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" reporting_database_build.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" etl_build.yml --vault-password-file ~/allu_keys/vault_secret
fi
