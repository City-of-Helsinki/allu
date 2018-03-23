#!/bin/bash

{% if 'production' in group_names %}

AUTOSSH_GATETIME=0
usr/bin/autossh -M 0 -o "ServerAliveInterval 30" -o "ServerAliveCountMax 3" -o StrictHostKeyChecking=no -N -L 8022:{{sap_ftp_invoice_host}}:{{sap_ftp_invoice_port}} allu@{{ ssh_tunnel_server_address | default('NA') }} -i {{ allu_ssh_key_file | default('NA') }}

{% else %}

sleep infinity

{% endif %}
