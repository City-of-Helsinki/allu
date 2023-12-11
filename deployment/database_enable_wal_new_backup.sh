#!/bin/bash
ansible-playbook -i production.inventory --private-key=$HOME/allu_keys/allu_id_rsa --extra-vars="var_branch=$1" database_enable_wal_new_backup.yml --vault-password-file ~/allu_keys/vault_secret
