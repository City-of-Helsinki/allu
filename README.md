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

- Recomendation is to open allu project into three projects when you develop it: backend, frontend and deployment

### Requirements ###
- NodeJS 12 or newer version
- Java 8 and 11
- Maven 3.3.9 or newer version
  - add to settings.xml file artifactory repositorie credentials: https://alluprojekti.atlassian.net/wiki/spaces/ALLU/pages/1933330/Kehitysymp+rist+n+pystytys
  - add toolchains.xml file to ~/.m2/ . Example down below
- Docker
- wkhtmltopdf

### Setting up toolchains ###
Example of toolchains.xml file . Change jdKHome path to were your designated version of Java 8 and 11 is.
```
<?xml version="1.0" encoding="UTF-8" ?>
<toolchains>
    <!-- JDK toolchains -->
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>8</version>
        </provides>
        <configuration>
            <jdkHome>path to Java 8 JDK</jdkHome>
        </configuration>
    </toolchain>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>11</version>
        </provides>
        <configuration>
            <jdkHome>path to Java 11 JDK</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

### Setting up Database ###
For database you need docker. Docker will start up postgresql and elasticsearch for project. Commands has been done in linux environment.  

Postgresql
1. you need to increase max virtualmemory for elastic search: edit file /etc/sysctl.conf by putting row: __vm.max_map_count=262144__ 
2. Go to folder allu/deployment/roles/database/files/database_docker/
3. run docker-compose up



### Setting up backend
will be found under backend folder and bare minimun is to start 4 service:
- allu-ui-service
- model-service
- search-service
- external-service

Starting class for each service can be found inside <name-of-service>/src/java/
You also need to install wkhtmltopdf and set it in /backend/pdf-service/src/resources/pd-service.properties.
There you change pdf.generator to point where you installed wkhtmltopdf. 
Correct version of wkhtmltopdf can be found deployment/roles/backend/files/wkhtmltox-0.12.3_linux-generic-amd64.tar.xz
If you want swagger to work you need to star:
- supervision-api  
url to swagger: http://localhost:9040/api-docs/swagger.json

### Setting up frontend ###
Go to folder frontend.
1. npm install -g @angular/cli
2. install dedendencies: npm install
2. start frontend: npm run start  
Optional: if you want hot reload run npm run hmr

When you have database, backend and frontend running you can access to site from localhost:3000/login.
Username:allute


## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details