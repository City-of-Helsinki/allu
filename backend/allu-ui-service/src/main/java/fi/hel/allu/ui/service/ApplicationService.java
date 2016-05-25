package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Person;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.ui.domain.Application;
import fi.hel.allu.ui.domain.ApplicationDTO;
import fi.hel.allu.ui.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;

@Service
public class ApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Value("${domain.application.create.url}")
    private String createApplicationUrl;

    @Value("${domain.person.create.url}")
    private String createPersonUrl;

    @Value("${domain.project.create.url}")
    private String createProjectUrl;

    @Value("${domain.application.findbyid.url}")
    private String findApplicationByIdUrl;

    @Value("${domain.application.findbyhandler.url}")
    private String findApplicationByHandlerUrl;

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
            if (applicationUI.getCustomer().getId() == null) {
                Person personModel = restTemplate.postForObject(createPersonUrl, createPersonModel(applicationUI), Person.class);
                applicationUI.getCustomer().setId(personModel.getId().toString());
            }

            if (applicationUI.getProject() == null || applicationUI.getProject().getId() == null) {
                Project projectModel = restTemplate.postForObject(createProjectUrl, createProjectModel(applicationUI), Project.class);
                mapProjectModelDomainToUiDomain(applicationUI, projectModel);
            }

            fi.hel.allu.model.domain.Application applicationDomain = restTemplate.postForObject(createApplicationUrl, createApplicationModel
                    (applicationUI), fi.hel.allu.model.domain.Application.class);

            applicationUI.setId(applicationDomain.getApplicationId().toString());
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
        return restTemplate.getForObject(findApplicationByIdUrl, ApplicationDTO.class, applicationId);
    }

    /**
     * Find given handler's active applications.
     *
     * @param handlerId handler identifier that is used to find details
     * @return List of found application with details
     */
    public ApplicationDTO findApplicationByHandler(String handlerId) {
        return restTemplate.getForObject(findApplicationByHandlerUrl, ApplicationDTO.class, handlerId);
    }


    private Person createPersonModel(Application applicationUI) {
        Customer customerUI = applicationUI.getCustomer();
        Person personDomain = new Person();
        personDomain.setCity(customerUI.getCity());
        personDomain.setEmail(customerUI.getEmail());
        personDomain.setFirstName(customerUI.getName());
        personDomain.setSsn(customerUI.getSsn());
        personDomain.setStreetAddress(customerUI.getAddress());
        personDomain.setZipCode(customerUI.getZipCode());
        return personDomain;
    }

    private Project createProjectModel(Application applicationUI) {
        Project projectDomain = new Project();
        if (applicationUI.getProject() == null || applicationUI.getProject().getName() == null) {
            projectDomain.setProjectName("Mock Project");
        } else {
            projectDomain.setProjectName(applicationUI.getProject().getName());
        }
        return projectDomain;
    }

    private fi.hel.allu.model.domain.Application createApplicationModel(Application applicationUI) {
        fi.hel.allu.model.domain.Application applicationDomain = new fi.hel.allu.model.domain.Application();
        applicationDomain.setName(applicationUI.getName());
        applicationDomain.setProjectId(Integer.parseInt(applicationUI.getProject().getId()));
        applicationDomain.setCreationTime(ZonedDateTime.now());
        applicationDomain.setCustomerId(Integer.parseInt(applicationUI.getCustomer().getId()));
        applicationDomain.setDescription(applicationUI.getInformation());
        applicationDomain.setHandler(applicationUI.getHandler());
        applicationDomain.setType(applicationUI.getType());
        return applicationDomain;
    }


    private void mapProjectModelDomainToUiDomain(Application applicationUI, Project projectDomain) {
        fi.hel.allu.ui.domain.Project projectUI = new fi.hel.allu.ui.domain.Project();
        projectUI.setId(projectDomain.getProjectId().toString());
        projectUI.setName(projectDomain.getProjectName());
        applicationUI.setProject(projectUI);
    }
}
