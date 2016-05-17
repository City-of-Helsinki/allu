#!/bin/bash
ansible-playbook -i test.inventory imageprep.yml --private-key $HOME/allu_keys/allu_id_rsa --vault-password-file ~/allu_keys/vault_secret --extra-vars "root_public_key=$HOME/allu_keys/allu_id_rsa.pub allu_public_key=$HOME/allu_keys/allu_id_rsa.pub"
