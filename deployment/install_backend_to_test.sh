#!/bin/bash
sh -e install_imageprep_to_test.sh dbservers
ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa database.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa backend.yml --vault-password-file ~/allu_keys/vault_secret
