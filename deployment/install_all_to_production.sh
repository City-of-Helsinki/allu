#!/bin/bash
# "dbservers" runs on same machine as "searchservers", so no need for "searchservers" imageprep
sh -e install_imageprep_to_production.sh dbservers
sh -e install_imageprep_to_production.sh backendservers
sh -e install_imageprep_to_production.sh webservers
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/prod_allu_id_rsa database.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/prod_allu_id_rsa elasticsearch.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/prod_allu_id_rsa backend.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/prod_allu_id_rsa frontend.yml --vault-password-file ~/allu_keys/vault_secret
