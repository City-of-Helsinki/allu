# Allu backend

## Build
To build backend, run `mvn clean install` in this (/backend) directory.

## Deploy locally with docker-compose
When you have built the backend, you can then use the jar files to deploy them to your local environment.
Prerequisites for deploying to local environment:

- `backend/<service>/src/main/resources/application.properties` for `allu-ui-service`, `external-service` and `supervision-api` has properties `wfs.username` and `wfs.password`.
Values are retrieved from the settings.xml which you created during backend building process.
For reference, usable values can be found in the `deployment/group_vars/test/credentials.yml` file.
Keyfile for reading the credentials files can be found by following the instructions in `deployment/install_backend_to_test.sh`
Ansible script, and by knowing where the CD pipelines of the project are run.
- Start database and Elasticsearch by running `docker-compose up` in `deployment/roles/database/files/database_docker`.

## Testing data
Testing data may be loaded onto the database by running test cases from `allu-ui-service-test` directory.
Running these tests requires the `frontend`, `allu-ui-service`, and `model-service` to be running.
Add `search-service` for good measure.

Go to the `allu-ui-service-test` directory: `cd backend/allu-ui-service-test/`.
Directory should have package.json file. Run `npm install`.

To run 10 different applications into the database, run `TEST_TARGET=http://localhost:3000 npm test`.
To run the massdata test, run `TEST_TARGET=http://localhost:3000 npm test "massdata/*"`. Note that it takes a long time,
as it inserts 10 000 applications. 