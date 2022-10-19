#!/bin/bash
# "backendservers" runs on same machine as "searchservers", so no need for "searchservers" imageprep
sh -e install_imageprep_to_production.sh dbservers
sh -e install_imageprep_to_production.sh backendservers
sh -e install_imageprep_to_production.sh webservers
# No imageprep to reporting db server, requires modified sshd_config

ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa docker_network/db_docker_network.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa database.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa elasticsearch.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa docker_network/backend_docker_network.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa backend.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa docker_network/web_docker_network.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa frontend.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa docker_network/reporting_db_docker_network.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa reporting_database.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa etl.yml --vault-password-file ~/allu_keys/vault_secret
