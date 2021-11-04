# Allu

Allu is a system to manage public area usage in Helsinki.
It is used to handle, grant, monitor and manage rights and notifications to use the public areas. Billing for using the area is also handled via Allu.

Governed public areas include:
- street
- sidewalk
- the bike path
- green area work
- an event to be organized
- land for rent

## Setting up Local Environment ##

### Requirements ###
- NodeJS 8.9.1 or newer version
- Java 1.8
- Maven 3.3.9 or newer version
  - add to settings.xml file artifactory repositorie credentials: https://alluprojekti.atlassian.net/wiki/spaces/ALLU/pages/1933330/Kehitysymp+rist+n+pystytys
- Docker

### Setting up Database ###
For database you need postresql and elasticsearch. Commands has been done in linux environment.  

Postgresql
1. Go to folder allu/deployment/roles/database/files/database_docker/
2. Build docker using the Dockerfile on folder : docker build -t allu_database .
3. Run build docker image:  docker run --rm -P -p 5432:5432 -d allu_database

Elasticsearch

you need to increase max virtualmemory for elastic search:
edit file /etc/sysctl.conf by putting row: __vm.max_map_count=262144__

### Setting up backend
will be found under backend folder and bare minimun is to start 4 service:
- allu-ui-service
- model-service
- search-service
- external-service

Starting class can be insidi <name-of-service>/src/java/  
If you want swagger to worl you need to star:
- supervision-api  
url to swagger: http://localhost:9040/api-docs/swagger.json

### Setting up frontend##
Go to folder frontend.
1. install dedendencies: npm install
2. start frontend: npm run start  
Optional: if you want hot reload run npm run hmr


## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details