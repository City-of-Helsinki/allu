#!/bin/bash
sh -e install_imageprep_to_staging.sh dbservers
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa docker_network/backend_docker_network.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa database.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa elasticsearch.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa backend.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa frontend.yml --vault-password-file ~/allu_keys/vault_secret
