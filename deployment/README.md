# How to deploy

## To local environment
To ensure proper deployment with Ansible, it is recommended to run Python in its own virtual environment (venv).
Install these to ensure Ansible works properly:
- Ansible version 5 (core 2.12.10)
- Python version 3.6.0

To deploy locally, run from current (deployment) directory:
```
./build_prep_all_for_localhost.sh
# Might want to run `docker network prune` at this point
# Activate Python venv that has ansible installed
source ~/venvs/ansible/bin/activate
# install modules
sudo apt install libpq5 libpq-dev python3-dev
pip3 install psycopg2
# May need to use community.postgresql version >=2.2.0
ansible-galaxy collection install --upgrade community.postgresql
# Build Docker images and deploy
./install_all_to_localhost.sh
```
Everything should now be running on your local environment.