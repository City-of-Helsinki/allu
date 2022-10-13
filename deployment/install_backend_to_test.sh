#!/bin/bash
sh -e install_imageprep_to_test.sh dbservers
if [ -z $1 ]; then
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa docker_network/backend_docker_network.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa database.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa elasticsearch.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa backend.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa sftpserver.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa reporting_database.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa etl.yml --vault-password-file ~/allu_keys/vault_secret
else
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa docker_network/backend_docker_network.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" database.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" elasticsearch.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" backend.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" sftpserver.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" reporting_database.yml --vault-password-file ~/allu_keys/vault_secret
  ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa etl.yml --vault-password-file ~/allu_keys/vault_secret
fi
