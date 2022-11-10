#!/bin/bash
ansible-playbook -i test.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" frontend_build.yml --vault-password-file ~/allu_keys/vault_secret
