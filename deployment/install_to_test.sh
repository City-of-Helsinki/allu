#!/bin/bash
ansible-playbook -i test.inventory imageprep.yml --private-key allu_id_rsa --ask-pass --extra-vars "root_public_key=$HOME/allu_keys/allu_id_rsa.pub allu_public_key=$HOME/allu_keys/allu_id_rsa.pub"
# ansible-playbook -i test.inventory frontend.yml --ask-sudo-pass --ask-pass

