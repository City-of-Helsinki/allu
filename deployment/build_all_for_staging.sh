#!/bin/bash
#ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" database_build.yml --vault-password-file ~/allu_keys/vault_secret
#ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" elasticsearch_build.yml --vault-password-file ~/allu_keys/vault_secret
ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" backend_build.yml --vault-password-file ~/allu_keys/vault_secret
#ansible-playbook -i staging.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" frontend_build.yml --vault-password-file ~/allu_keys/vault_secret