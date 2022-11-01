# Allu backend

## Build
To build backend, run "mvn clean install" in this directory.

## Deploy locally with docker-compose
When you have built the backend, you can then use the jar files to deploy them to your local environment.
Prerequisites for deploying to local environment:
- Fix in `backend/<service>/src/main/resources/application.properties` for
`allu-ui-service`, `external-service` and `supervision-api` properties `wfs.username` and `wfs.password`.
Usable values can be found in the `deployment/group_vars/test/credentials.yml` file.
Keyfile to credentials files can be found by following the instructions in `deployment/install_backend_to_test.sh`
Ansible script, and by knowing where the CD pipelines of the project are run.
- Fix in `backend/pdf-service/src/main/resources/application.properties` properties `pdf.tempdir` and `pdf.stylesheetdir`.
Usable values can be found in the `deployment/roles/backend/templates/pdf-service.properties` file.
- Start database and Elasticsearch by running `docker-compose up` in `deployment/roles/database/files/database_docker`.

To deploy, run the following commands:
```
sudo ./copy_pdf_stylesheets_for_docker-compose.sh
docker-compose up
```
The script has to be run to ensure that all stylesheets are included. They could not be included in the Dockerfile,
as all files used for the Dockerfile need to be within the same folder it is in. Thus, the stylesheets are copied to
another directory, to ensure that they are not modified further while testing, and then linked within the Dockerfile
to a Docker volume.

## Testing data
Testing data may be loaded onto the database by running test cases from `allu-ui-service-test` directory.
Running these tests requires the `frontend`, `allu-ui-service`, and `model-service` to be running.
Add `search-service` for good measure.

To run 10 different applications into the database, run `TEST_TARGET=http://localhost:3000 npm test`.
To run the massdata test, run `TEST_TARGET=http://localhost:3000 npm test "massdata/*"`. Note that it takes a long time,
as it inserts 10 000 applications. 