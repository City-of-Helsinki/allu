package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ApplicationListJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    private ApplicationProperties applicationProperties;
    private RestTemplate restTemplate;
    private LocationService locationService;
    private CustomerService customerService;
    private ApplicantService applicantService;
    private ProjectService projectService;

    @Autowired
    public void ApplicationService(ApplicationProperties applicationProperties, RestTemplate restTemplate, LocationService
        locationService, CustomerService customerService, ApplicantService applicantService, ProjectService projectService) {
        this.applicationProperties = applicationProperties;
        this.restTemplate = restTemplate;
        this.locationService = locationService;
        this.customerService = customerService;
        this.applicantService = applicantService;
        this.projectService = projectService;
    }


    /**
     * Create applications by calling backend service.
     *
     * @param applications Transfer object that contains list of applications that are going to be created
     * @return Transfer object that contains list of created applications and their identifiers
     */
    public ApplicationListJson createApplication(ApplicationListJson applications) {

        for (ApplicationJson applicationJson : applications.getApplicationList()) {
            applicationJson.setCustomer(customerService.createCustomer(applicationJson.getCustomer()));
            applicationJson.setProject(projectService.createProject(applicationJson.getProject()));
            applicationJson.setApplicant(applicantService.createApplicant(applicationJson.getApplicant()));
            applicationJson.setLocation(locationService.createLocation(applicationJson.getLocation()));

            Application applicationModel = restTemplate.postForObject(applicationProperties
                    .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
                    createApplicationModel(applicationJson), Application.class);

            applicationJson.setId(applicationModel.getId());
        }
        return applications;
    }

    /**
     * Update the given application by calling backend service.
     *
     * @param applicationJson application that is going to be updated
     * @return Updated application
     */
    public ApplicationJson updateApplication(int applicationId, ApplicationJson applicationJson) {
        customerService.updateCustomer(applicationJson.getCustomer());
        applicantService.updateApplicant(applicationJson.getApplicant());
        projectService.updateProject(applicationJson.getProject());
        locationService.updateLocation(applicationJson.getLocation());

        restTemplate.put(applicationProperties.getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_UPDATE), createApplicationModel
            (applicationJson), applicationId);
        return applicationJson;
    }


    /**
     * Find given application details.
     *
     * @param applicationId application identifier that is used to find details
     * @return Application details or empty application list in DTO
     */
    public ApplicationJson findApplicationById(String applicationId) {
        Application applicationModel = restTemplate.getForObject(applicationProperties
            .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_ID), Application.class, applicationId);
        return getApplication(applicationModel);
    }

    /**
     * Find given handler's active applications.
     *
     * @param handlerId handler identifier that is used to find details
     * @return List of found application with details
     */
    public List<ApplicationJson> findApplicationByHandler(String handlerId) {
        List<ApplicationJson> resultList = new ArrayList<>();
        ResponseEntity<Application[]> applicationResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_HANDLER), Application[].class, handlerId);
            for (Application applicationModel : applicationResult.getBody()) {
                resultList.add(getApplication(applicationModel));
            }
        return resultList;
    }

    private ApplicationJson getApplication(Application applicationModel) {
        ApplicationJson applicationJson = new ApplicationJson();
        mapApplicationToJson(applicationJson, applicationModel);
        applicationJson.setCustomer(customerService.findCustomerById(applicationModel.getCustomerId()));
        applicationJson.setProject(projectService.findProjectById(applicationModel.getProjectId()));
        applicationJson.setApplicant(applicantService.findApplicantById(applicationModel.getApplicantId()));
        if (applicationModel.getLocationId() != null && applicationModel.getLocationId() > 0) {
            applicationJson.setLocation(locationService.findLocationById(applicationModel.getLocationId()));
        }
        return applicationJson;
    }

    private Application createApplicationModel(ApplicationJson applicationJson) {
        Application applicationDomain = new fi.hel.allu.model.domain.Application();
        if (applicationJson.getId() != null) {
            applicationDomain.setId(applicationJson.getId());
        }
        applicationDomain.setName(applicationJson.getName());
        applicationDomain.setProjectId(applicationJson.getProject().getId());
        applicationDomain.setCreationTime(ZonedDateTime.now());
        applicationDomain.setCustomerId(applicationJson.getCustomer().getId());
        applicationDomain.setApplicantId(applicationJson.getApplicant().getId());
        applicationDomain.setHandler(applicationJson.getHandler());
        applicationDomain.setType(applicationJson.getType());
        applicationDomain.setStatus(applicationJson.getStatus());
        if (applicationJson.getLocation() != null && applicationJson.getLocation().getId() != null) {
            applicationDomain.setLocationId(applicationJson.getLocation().getId());
        }
        return applicationDomain;
    }

    private void mapApplicationToJson(ApplicationJson applicationJson, Application application) {
        applicationJson.setId(application.getId());
        applicationJson.setStatus(application.getStatus());
        applicationJson.setType(application.getType());
        applicationJson.setHandler(application.getHandler());
        applicationJson.setCreationTime(application.getCreationTime());
        applicationJson.setName(application.getName());
    }
}

