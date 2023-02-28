#!/bin/bash
sh -e install_imageprep_to_staging.sh dbservers
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa docker_network/backend_docker_network.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" database_deploy.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" elasticsearch_deploy.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" backend_deploy.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" frontend_deploy.yml --vault-password-file ~/allu_keys/vault_secret
