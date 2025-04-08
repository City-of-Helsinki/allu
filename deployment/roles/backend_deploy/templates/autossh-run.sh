#!/bin/bash

{% if 'test' in group_names %}

echo "SSH tunnel not in use"

{% else %}

sleep infinity

{% endif %}
