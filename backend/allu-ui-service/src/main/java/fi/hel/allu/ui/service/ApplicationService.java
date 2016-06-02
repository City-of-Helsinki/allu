package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Person;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.Application;
import fi.hel.allu.ui.domain.ApplicationDTO;
import fi.hel.allu.ui.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;

@Service
public class ApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Autowired
    private ApplicationProperties applicationProperties;


    @Autowired
    private RestTemplate restTemplate;


    /**
     * Create applications by calling backend service.
     *
     * @param applications List of applications that are going to be created
     * @return List of created applications and their identifiers
     */
    public ApplicationDTO createApplication(ApplicationDTO applications) {

        for (Application applicationUI : applications.getApplicationList()) {
            if (applicationUI.getCustomer().getId() == 0) {
                Person personModel = restTemplate.postForObject(applicationProperties
                        .getUrl(ApplicationProperties.PATH_MODEL_PERSON_CREATE), createPersonModel(applicationUI), Person.class);
                applicationUI.getCustomer().setId(personModel.getId());
            }

            if (applicationUI.getProject() == null || applicationUI.getProject().getId() == 0) {
                Project projectModel = restTemplate.postForObject(applicationProperties
                        .getUrl(ApplicationProperties.PATH_MODEL_PROJECT_CREATE), createProjectModel(applicationUI), Project.class);
                fi.hel.allu.ui.domain.Project projectUI = new fi.hel.allu.ui.domain.Project();
                mapProjectModelToUi(projectUI, projectModel);
                applicationUI.setProject(projectUI);
            }

            fi.hel.allu.model.domain.Application applicationModel = restTemplate.postForObject(applicationProperties
                    .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
                    createApplicationModel
                    (applicationUI), fi.hel.allu.model.domain.Application.class);

            applicationUI.setId(applicationModel.getId());
        }
        return applications;
    }


    /**
     * Find given application details.
     *
     * @param applicationId application identifier that is used to find details
     * @return Application details or empty application list in DTO
     */
    public ApplicationDTO findApplicationById(String applicationId) {
        ApplicationDTO resultDTO = new ApplicationDTO();
        fi.hel.allu.model.domain.Application applicationModel =  restTemplate.getForObject(applicationProperties
                        .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_ID), fi.hel.allu.model.domain.Application.class,
                applicationId);
        resultDTO.getApplicationList().add(getApplication(applicationModel));
        return resultDTO;
    }

    /**
     * Find given handler's active applications.
     *
     * @param handlerId handler identifier that is used to find details
     * @return List of found application with details
     */
    public ApplicationDTO findApplicationByHandler(String handlerId) {
        ApplicationDTO resultDTO = new ApplicationDTO();
        ResponseEntity<fi.hel.allu.model.domain.Application[]> applicationResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_HANDLER), fi
                .hel.allu.model.domain.Application[].class, handlerId);
            for (fi.hel.allu.model.domain.Application applicationModel : applicationResult.getBody()) {
                resultDTO.getApplicationList().add(getApplication(applicationModel));
            }
        return resultDTO;
    }

    private Application getApplication(fi.hel.allu.model.domain.Application applicationModel) {
        Application applicationUI = new Application();
        mapApplicationModelToUi(applicationUI, applicationModel);
        applicationUI.setCustomer(findPersonById(applicationModel.getCustomerId()));
        applicationUI.setProject(findProjectById(applicationModel.getProjectId()));
        return applicationUI;
    }

    private Customer findPersonById(int personId) {
        Customer customerUI = new Customer();
        ResponseEntity<Person> personResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_PERSON_FIND_BY_ID), Person.class, personId);
        mapPersonToCustomer(customerUI, personResult.getBody());
        return customerUI;
    }

    private fi.hel.allu.ui.domain.Project findProjectById(int projectId)  {
        fi.hel.allu.ui.domain.Project projectUI = new fi.hel.allu.ui.domain.Project();
        ResponseEntity<Project> projectResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_PROJECT_FIND_BY_ID), Project.class, projectId);
        mapProjectModelToUi(projectUI, projectResult.getBody());
        return projectUI;
    }

    private Person createPersonModel(Application applicationUI) {
        Customer customerUI = applicationUI.getCustomer();
        Person personDomain = new Person();
        personDomain.setCity(customerUI.getCity());
        personDomain.setEmail(customerUI.getEmail());
        personDomain.setName(customerUI.getName());
        personDomain.setSsn(customerUI.getSsn());
        personDomain.setStreetAddress(customerUI.getAddress());
        personDomain.setPostalCode(customerUI.getZipCode());
        return personDomain;
    }

    private Project createProjectModel(Application applicationUI) {
        Project projectDomain = new Project();
        if (applicationUI.getProject() == null || applicationUI.getProject().getName() == null) {
            projectDomain.setName("Mock Project");
        } else {
            projectDomain.setName(applicationUI.getProject().getName());
        }
        return projectDomain;
    }

    private fi.hel.allu.model.domain.Application createApplicationModel(Application applicationUI) {
        fi.hel.allu.model.domain.Application applicationDomain = new fi.hel.allu.model.domain.Application();
        applicationDomain.setName(applicationUI.getName());
        applicationDomain.setProjectId(applicationUI.getProject().getId());
        applicationDomain.setCreationTime(ZonedDateTime.now());
        applicationDomain.setCustomerId(applicationUI.getCustomer().getId());
        applicationDomain.setHandler(applicationUI.getHandler());
        applicationDomain.setType(applicationUI.getType());
        return applicationDomain;
    }


    private void mapProjectModelToUi(fi.hel.allu.ui.domain.Project projectUI, Project projectDomain) {
        projectUI.setId(projectDomain.getId());
        projectUI.setName(projectDomain.getName());
    }

    private void mapPersonToCustomer(Customer customerUI, Person personDomain) {
        customerUI.setName(personDomain.getName());
        customerUI.setAddress(personDomain.getStreetAddress());
        customerUI.setCity(personDomain.getCity());
        customerUI.setEmail(personDomain.getEmail());
        customerUI.setId(personDomain.getId());
        customerUI.setZipCode(personDomain.getPostalCode());
        customerUI.setSsn(personDomain.getSsn());
    }

    private void mapApplicationModelToUi(Application applicationUI, fi.hel.allu.model.domain.Application applicationDomain) {
        applicationUI.setId(applicationDomain.getId());
        applicationUI.setType(applicationDomain.getType());
        applicationUI.setHandler(applicationDomain.getHandler());
        applicationUI.setCreateDate(applicationDomain.getCreationTime());
        applicationUI.setName(applicationDomain.getName());
        applicationUI.setStatus(applicationDomain.getStatus());
    }
}
