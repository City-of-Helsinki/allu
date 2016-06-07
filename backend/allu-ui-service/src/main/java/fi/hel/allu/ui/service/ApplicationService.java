package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.*;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.validator.CustomerValidator;
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
    public List<ApplicationJson> createApplication(ApplicationListJson applications) {

        for (ApplicationJson applicationJson : applications.getApplicationList()) {
            if (applicationJson.getCustomer().getId() == 0) {
                CustomerValidator.validateCustomer(applicationJson.getCustomer());
                Customer customerModel = createNewCustomer(applicationJson.getCustomer());
                applicationJson.getCustomer().setId(customerModel.getId());
            }

            if (applicationJson.getProject() == null || applicationJson.getProject().getId() == 0) {
                Project projectModel = restTemplate.postForObject(applicationProperties
                        .getUrl(ApplicationProperties.PATH_MODEL_PROJECT_CREATE), createProjectModel(applicationJson.getProject()), Project.class);
                ProjectJson projectJson = new ProjectJson();
                mapProjectToJson(projectJson, projectModel);
                applicationJson.setProject(projectJson);
            }

            if (applicationJson.getApplicant().getId() == 0) {
                Applicant applicantModel = createNewApplicant(applicationJson.getApplicant());
                applicationJson.getApplicant().setId(applicantModel.getId());
            }

            if (applicationJson.getLocation() != null && applicationJson.getLocation().getId() == 0) {
                Location location = restTemplate.postForObject(applicationProperties
                        .getUrl(ApplicationProperties.PATH_MODEL_LOCATION_CREATE), createLocationModel(applicationJson.getLocation()),
                        Location.class);
                applicationJson.getLocation().setId(location.getId());
            }

            Application applicationModel = restTemplate.postForObject(applicationProperties
                    .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
                    createApplicationModel(applicationJson), Application.class);

            applicationJson.setId(applicationModel.getId());
        }
        return applications.getApplicationList();
    }


    /**
     * Find given application details.
     *
     * @param applicationId application identifier that is used to find details
     * @return Application details or empty application list in DTO
     */
    public ApplicationJson findApplicationById(String applicationId) {
        Application applicationModel =  restTemplate.getForObject(applicationProperties
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
        applicationJson.setCustomer(findCustomerById(applicationModel.getCustomerId()));
        applicationJson.setProject(findProjectById(applicationModel.getProjectId()));
        applicationJson.setApplicant(findApplicantById(applicationModel.getApplicantId()));
        if (applicationModel.getLocationId() != null && applicationModel.getLocationId() > 0) {
            applicationJson.setLocation(findLocationById(applicationModel.getLocationId()));
        }
        return applicationJson;
    }

    private PersonJson findPersonById(int personId) {
        PersonJson personJson = new PersonJson();
        ResponseEntity<Person> personResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_PERSON_FIND_BY_ID), Person.class, personId);
        mapPersonToJson(personJson, personResult.getBody());
        return personJson;
    }

    private ApplicantJson findApplicantById(int applicantId) {
        ApplicantJson applicantJson = new ApplicantJson();
        ResponseEntity<Applicant> applicantResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_APPLICANT_FIND_BY_ID), Applicant.class, applicantId);
        mapApplicantToJson(applicantJson, applicantResult.getBody());
        if (applicantResult.getBody().getPersonId() != null) {
            applicantJson.setPerson(findPersonById(applicantResult.getBody().getPersonId()));
        }
        if (applicantResult.getBody().getOrganizationId() != null) {
            applicantJson.setOrganization(findOrganizationById(applicantResult.getBody().getOrganizationId()));
        }
        return applicantJson;
    }

    private OrganizationJson findOrganizationById(int organizationId) {
        OrganizationJson organizationJson = new OrganizationJson();
        ResponseEntity<Organization> organizationResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_ORGANIZATION_FIND_BY_ID), Organization.class, organizationId);
        mapOrganizationToJson(organizationJson, organizationResult.getBody());
        return organizationJson;
    }

    private CustomerJson findCustomerById(int customerId) {
        CustomerJson customerJson = new CustomerJson();
        ResponseEntity<Customer> customerResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_CUSTOMER_FIND_BY_ID), Customer.class, customerId);
        mapCustomerToJson(customerJson, customerResult.getBody());
        if (customerResult.getBody().getOrganizationId() != null) {
            customerJson.setOrganization(findOrganizationById(customerResult.getBody().getOrganizationId()));
        }
        if (customerResult.getBody().getPersonId() != null) {
            customerJson.setPerson(findPersonById(customerResult.getBody().getPersonId()));
        }
        return customerJson;
    }


    private ProjectJson findProjectById(int projectId)  {
        ProjectJson projectJson = new ProjectJson();
        ResponseEntity<Project> projectResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_PROJECT_FIND_BY_ID), Project.class, projectId);
        mapProjectToJson(projectJson, projectResult.getBody());
        return projectJson;
    }

    private LocationJson findLocationById(int locationId)  {
        LocationJson locationJson = new LocationJson();
        ResponseEntity<Location> locationResult = restTemplate.getForEntity(applicationProperties
                .getUrl(ApplicationProperties.PATH_MODEL_LOCATION_FIND_BY_ID), Location.class, locationId);
        mapLocationToJson(locationJson, locationResult.getBody());
        return locationJson;
    }

    private Customer createCustomerModel(CustomerJson customerJson) {
        Customer customerModel = new Customer();
        customerModel.setSapId(customerJson.getSapId());
        customerModel.setType(customerJson.getType());
        if (customerJson.getOrganization() != null) {
            customerModel.setOrganizationId(customerJson.getOrganization().getId());
        }
        if (customerJson.getPerson() != null) {
            customerModel.setPersonId(customerJson.getPerson().getId());
        }
        return customerModel;
    }

    private Organization createOrganizationModel(OrganizationJson organizationJson) {
        Organization organizationModel = new Organization();
        organizationModel.setPostalCode(organizationJson.getPostalCode());
        organizationModel.setStreetAddress(organizationJson.getStreetAddress());
        organizationModel.setPhone(organizationJson.getPhone());
        organizationModel.setName(organizationJson.getName());
        organizationModel.setBusinessId(organizationJson.getBusinessId());
        organizationModel.setCity(organizationJson.getCity());
        organizationModel.setEmail(organizationJson.getEmail());
        return organizationModel;
    }

    private Person createPersonModel(PersonJson personJson) {
        Person personModel = new Person();
        personModel.setStreetAddress(personJson.getStreetAddress());
        personModel.setPhone(personJson.getPhone());
        personModel.setName(personJson.getName());
        personModel.setSsn(personJson.getSsn());
        personModel.setCity(personJson.getCity());
        personModel.setEmail(personJson.getEmail());
        personModel.setPostalCode(personJson.getPostalCode());
        return personModel;
    }

    private Applicant createApplicantModel(ApplicantJson applicantJson) {
        Applicant applicantModel = new Applicant();
        if (applicantJson.getPerson() != null) {
            applicantModel.setPersonId(applicantJson.getPerson().getId());
        }
        if (applicantJson.getOrganization() != null) {
            applicantModel.setOrganizationId(applicantJson.getOrganization().getId());
        }
        return applicantModel;
    }

    private Project createProjectModel(ProjectJson projectJson) {
        Project projectDomain = new Project();
        if (projectJson == null || projectJson.getName() == null) {
            projectDomain.setName("Mock Project");
        } else {
            projectDomain.setName(projectJson.getName());
        }
        return projectDomain;
    }

    private Application createApplicationModel(ApplicationJson applicationJson) {
        Application applicationDomain = new fi.hel.allu.model.domain.Application();
        applicationDomain.setName(applicationJson.getName());
        applicationDomain.setProjectId(applicationJson.getProject().getId());
        applicationDomain.setCreationTime(ZonedDateTime.now());
        applicationDomain.setCustomerId(applicationJson.getCustomer().getId());
        applicationDomain.setApplicantId(applicationJson.getApplicant().getId());
        applicationDomain.setHandler(applicationJson.getHandler());
        applicationDomain.setType(applicationJson.getType());
        applicationDomain.setStatus(applicationJson.getStatus());
        if (applicationJson.getLocation() != null && applicationJson.getLocation().getId() > 0) {
            applicationDomain.setLocationId(applicationJson.getLocation().getId());
        }
        return applicationDomain;
    }

    private Location createLocationModel(LocationJson locationJson) {
        Location location = new Location();
        location.setStreetAddress(locationJson.getStreetAddress());
        location.setPostalCode(locationJson.getPostalCode());
        location.setGeometry(locationJson.getGeometry());
        location.setCity(locationJson.getCity());
        return location;
    }

    private void mapLocationToJson(LocationJson locationJson, Location location) {
        locationJson.setId(location.getId());
        locationJson.setCity(location.getCity());
        locationJson.setGeometry(location.getGeometry());
        locationJson.setPostalCode(location.getPostalCode());
        locationJson.setStreetAddress(location.getStreetAddress());
    }

    private void mapProjectToJson(ProjectJson projectJson, Project projectDomain) {
        projectJson.setId(projectDomain.getId());
        projectJson.setName(projectDomain.getName());
    }

    private void mapPersonToJson(PersonJson personJson, Person personDomain) {
        personJson.setId(personDomain.getId());
        personJson.setPostalCode(personDomain.getPostalCode());
        personJson.setStreetAddress(personDomain.getStreetAddress());
        personJson.setSsn(personDomain.getSsn());
        personJson.setPhone(personDomain.getPhone());
        personJson.setName(personDomain.getName());
        personJson.setEmail(personDomain.getEmail());
        personJson.setCity(personDomain.getCity());
    }

    private void mapCustomerToJson(CustomerJson customerJson, Customer customer) {
        customerJson.setId(customer.getId());
        customerJson.setType(customer.getType());
        customerJson.setSapId(customer.getSapId());
    }

    private void mapApplicantToJson(ApplicantJson applicantJson, Applicant applicant) {
        applicantJson.setId(applicant.getId());
    }

    private void mapOrganizationToJson(OrganizationJson organizationJson, Organization organization) {
        organizationJson.setId(organization.getId());
        organizationJson.setStreetAddress(organization.getStreetAddress());
        organizationJson.setPostalCode(organization.getPostalCode());
        organizationJson.setPhone(organization.getPhone());
        organizationJson.setName(organization.getName());
        organizationJson.setEmail(organization.getEmail());
        organizationJson.setCity(organization.getCity());
        organizationJson.setBusinessId(organization.getBusinessId());
    }

    private void mapContactToJson(ContactJson contactJson, Contact contact) {
        contactJson.setId(contact.getId());
    }

    private void mapApplicationToJson(ApplicationJson applicationJson, Application application) {
        applicationJson.setId(application.getId());
        applicationJson.setStatus(application.getStatus());
        applicationJson.setType(application.getType());
        applicationJson.setHandler(application.getHandler());
        applicationJson.setCreationTime(application.getCreationTime());
        applicationJson.setName(application.getName());
    }

    private Customer createNewCustomer(CustomerJson customerJson) {
        if (customerJson.getPerson() != null && customerJson.getPerson().getId() == 0) {
            Person personModel = restTemplate.postForObject(applicationProperties
                            .getUrl(ApplicationProperties.PATH_MODEL_PERSON_CREATE), createPersonModel(customerJson.getPerson()),
                    Person.class);
            mapPersonToJson(customerJson.getPerson(), personModel);
        }

        if (customerJson.getOrganization() != null && customerJson.getOrganization().getId() == 0) {
            Organization organizationModel = restTemplate.postForObject(applicationProperties
                            .getUrl(ApplicationProperties.PATH_MODEL_ORGANIZATION_CREATE), createOrganizationModel(customerJson.getOrganization()),
                    Organization.class);
            mapOrganizationToJson(customerJson.getOrganization(), organizationModel);
        }

        Customer customerModel = restTemplate.postForObject(applicationProperties
                        .getUrl(ApplicationProperties.PATH_MODEL_CUSTOMER_CREATE), createCustomerModel(customerJson),
                Customer.class);

        return customerModel;
    }

    private Applicant createNewApplicant(ApplicantJson applicantJson) {
        if (applicantJson.getPerson() != null && applicantJson.getPerson().getId() == 0) {
            Person personModel = restTemplate.postForObject(applicationProperties
                            .getUrl(ApplicationProperties.PATH_MODEL_PERSON_CREATE), createPersonModel(applicantJson.getPerson()),
                    Person.class);
            mapPersonToJson(applicantJson.getPerson(), personModel);
        }

        if (applicantJson.getOrganization() != null && applicantJson.getOrganization().getId() == 0) {
            Organization organizationModel = restTemplate.postForObject(applicationProperties
                            .getUrl(ApplicationProperties.PATH_MODEL_ORGANIZATION_CREATE), createOrganizationModel(applicantJson.getOrganization()),
                    Organization.class);
            mapOrganizationToJson(applicantJson.getOrganization(), organizationModel);
        }

        Applicant applicantModel = restTemplate.postForObject(applicationProperties
                        .getUrl(ApplicationProperties.PATH_MODEL_APPLICANT_CREATE), createApplicantModel(applicantJson),
                Applicant.class);

        return applicantModel;
    }
}

