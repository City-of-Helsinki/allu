# Start database for tests
cd roles/database/files/database_docker
docker-compose up -d

# Build backend services
cd ../../../../../backend
mvn install

# Copy jar files to deployment/roles/{role}/files/
cd ..
## allu-ui container
cp backend/allu-ui-service/target/allu-ui-service-1.0-SNAPSHOT.jar deployment/roles/backend/files/allu_ui_docker/allu-ui-service-1.0-SNAPSHOT.jar

## allu-backend container
cp backend/model-service/target/model-service-1.0-SNAPSHOT.jar deployment/roles/backend/files/backend_docker/model-service-1.0-SNAPSHOT.jar
cp backend/search-service/target/search-service-1.0-SNAPSHOT.jar deployment/roles/backend/files/backend_docker/search-service-1.0-SNAPSHOT.jar
cp backend/pdf-service/target/pdf-service-1.0-SNAPSHOT.jar deployment/roles/backend/files/backend_docker/pdf-service-1.0-SNAPSHOT.jar
cp backend/scheduler-service/target/scheduler-service-1.0-SNAPSHOT.jar deployment/roles/backend/files/backend_docker/scheduler-service-1.0-SNAPSHOT.jar
cp backend/external-service/target/external-service-1.0-SNAPSHOT.jar deployment/roles/backend/files/backend_docker/external-service-1.0-SNAPSHOT.jar
cp backend/supervision-api/target/supervision-api-1.0-SNAPSHOT.jar deployment/roles/backend/files/backend_docker/supervision-api-1.0-SNAPSHOT.jar

## ETL
cp backend/allu-etl/target/allu-etl-1.0-SNAPSHOT.jar deployment/roles/etl/files/etl/allu-etl-1.0-SNAPSHOT.jar
