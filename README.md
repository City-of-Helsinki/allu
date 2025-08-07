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

## Setting up Local Environment

It is recommended to open Allu project into three projects when you develop: backend, frontend and deployment.

### Requirements
- Node.js 20
- Java 17
- Maven 3.9.6 or newer version
  - add settings.xml file to path ~/.m2/. Example and artifactory credentials: https://helsinkisolutionoffice.atlassian.net/wiki/x/BwC72QE
  - add toolchains.xml file to path ~/.m2/. Example down below.
- Docker
- wkhtmltopdf

### Setting up toolchains.xml
Example of toolchains.xml file.
Change `<jdKHome>` path to where your designated version of Java 17 is. In Windows use two backslashes in the path `\\`.
```
<?xml version="1.0" encoding="UTF-8" ?>
<toolchains>
    <!-- JDK toolchains -->
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>17</version>
        </provides>
        <configuration>
            <jdkHome>path/to/java/jdk</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

### Setting up wkhtmltopdf
You need to install wkhtmltopdf and set it in backend/pdf-service/src/main/resources/application.properties

Current version of wkhtmltopdf in use can be found in deployment/roles/backend_build/files/pdf_service_docker/wkhtmltox-version_linux-generic-amd64.tar.xz

1. Open application.properties
2. Change `pdf.generator` to point where you installed wkhtmltopdf
```
pdf.generator=/opt/local/wkhtmltox/bin/wkhtmltopdf
```

### Setting up Database
For database, you need to install Docker. Docker will start up PostgreSQL and Elasticsearch for the project.
Below commands has been done in Linux environment.
> Linux users need to increase max virtual memory for Elasticsearch. Edit file /etc/sysctl.conf by putting row: __vm.max_map_count=262144__

#### PostgreSQL
Start the database and Elasticsearch:
```
cd deployment/roles/database_build/files/database_docker
docker compose up
```

_Database can be started without Elasticsearch with command:_
```
docker compose up allu_database
```

##### Troubleshooting
If you encounter a problem where locale.md cannot be found, try temporarily changing the code below in the Dockerfile. This will work as long as there is only one locale setting.

Error:
```
[allu_database 7/9] RUN cat locale.md | xargs -i /usr/glibc-compat/bin/localedef -i {} -f UTF-8 {}.UTF-8:
': No such file or directoryinition file `fi_FI
```

Temporary solution:
`RUN cat locale.md | xargs -i /usr/glibc-compat/bin/localedef -i {} -f UTF-8 {}.UTF-8`
->
`RUN /usr/glibc-compat/bin/localedef -i fi_FI -f UTF-8 fi_FI.UTF-8`

### Setting up backend
Go to backend folder:
```
cd backend
mvn install
```

Start backend with Docker:
```
docker compose up
```

Bare minimun is to start 4 services:

- allu-ui-service
- model-service
- search-service
- external-service

Starting class for each service can be found inside <name-of-service>/src/main/java/

If you want swagger to work you need to start:
- supervision-api  
URL to swagger: http://localhost:9040/api-docs/swagger.json

### Setting up frontend
Go to frontend folder, install dependencies and start frontend:
```
cd frontend
npm install -g @angular/cli
npm install
npm run start
```
Optional - if you want hot reload, run: `npm run hmr`

### Access Allu

When you have database, backend and frontend running you can access to site from localhost:3000/login

Username: allute

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.