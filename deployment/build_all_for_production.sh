#!/bin/bash
# "backendservers" runs on same machine as "searchservers", so no need for "searchservers" imageprep
sh -e install_imageprep_to_production.sh dbservers
sh -e install_imageprep_to_production.sh backendservers
sh -e install_imageprep_to_production.sh webservers
# No imageprep to reporting db server, requires modified sshd_config

ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" database_build.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" elasticsearch_build.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" backend_build.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" frontend_build.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" reporting_database_build.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" etl_build.yml --vault-password-file ~/allu_keys/vault_secret
