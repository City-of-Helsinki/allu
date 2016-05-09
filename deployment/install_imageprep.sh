#!/usr/bin/env bash
if [ -z $1 ]; then
  echo "Usage: $0 <target_host>"
else
  # note that the $1, is intentional. If only $1 would be used, Ansible would not recognise the parameter correctly
  ansible-playbook -i $1, imageprep.yml --private-key $HOME/allu_keys/allu_id_rsa --ask-pass --extra-vars "root_public_key=$HOME/allu_keys/allu_id_rsa.pub allu_public_key=$HOME/allu_keys/allu_id_rsa.pub"
fi
