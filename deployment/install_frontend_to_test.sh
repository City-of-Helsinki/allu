#!/bin/bash
sh -e install_imageprep_to_test.sh
ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa frontend.yml --vault-password-file ~/allu_keys/vault_secret
