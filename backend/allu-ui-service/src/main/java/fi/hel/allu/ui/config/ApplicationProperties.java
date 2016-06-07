package fi.hel.allu.ui.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

    private String modelServiceHost;

    private String modelServicePort;

    @Autowired
    public ApplicationProperties(@Value("${model.service.host}") @NotEmpty String modelServiceHost,
                                 @Value("${model.service.port}") @NotEmpty String modelServicePort) {
        this.modelServiceHost = modelServiceHost;
        this.modelServicePort = modelServicePort;
    }

    public static final String PATH_PREFIX = "http://";

    /**
     * Model-service path to create a new application
     */
    public static final String PATH_MODEL_APPLICATION_CREATE = "/applications";

    /**
     * Model-service path to find application by identifier
     */
    public static final String PATH_MODEL_APPLICATION_FIND_BY_ID = "/applications/{applicationId}";

    /**
     * Model-service path to find application by handler
      */
    public static final String PATH_MODEL_APPLICATION_FIND_BY_HANDLER = "/applications//byhandler/{handlerId}";

    /**
     * Model-service path to create a new person
     */
    public static final String PATH_MODEL_PERSON_CREATE = "/persons";

    /**
     * Model-service path to find person by identifier
     */
    public static final String PATH_MODEL_PERSON_FIND_BY_ID = "/persons/{personId}";

    /**
     * Model-service path to create a new project
     */
    public static final String PATH_MODEL_PROJECT_CREATE = "/projects";

    /**
     * Model-service path to find project by identifier
     */
    public static final String PATH_MODEL_PROJECT_FIND_BY_ID = "/projects/{projectId}";

    /**
     * Model-service path to create a new applicant
     */
    public static final String PATH_MODEL_APPLICANT_CREATE = "/applicants";

    /**
     * Model-service path to find applicant by identifier
     */
    public static final String PATH_MODEL_APPLICANT_FIND_BY_ID = "/applicants/{applicantId}";

    /**
     * Model-service path to create a new customer
     */
    public static final String PATH_MODEL_CUSTOMER_CREATE = "/customers";

    /**
     * Model-service path to find customer by identifier
     */
    public static final String PATH_MODEL_CUSTOMER_FIND_BY_ID = "/customers/{customerId}";

    /**
     * Model-service path to create a new organization
     */
    public static final String PATH_MODEL_ORGANIZATION_CREATE = "/organizations";

    /**
     * Model-service path to find organization by identifier
     */
    public static final String PATH_MODEL_ORGANIZATION_FIND_BY_ID = "/organizations/{organizationId}";

    /**
     * Model-service path to create a new contact
     */
    public static final String PATH_MODEL_CONTACT_CREATE = "/contacts";

    /**
     * Model-service path to find contact by identifier
     */
    public static final String PATH_MODEL_CONTACT_FIND_BY_ID = "/contacts/{contactId}";

    /**
     * Model-service path to find contacts by organization identifier
     */
    public static final String PATH_MODEL_CONTACT_FIND_BY_ORGANIZATION = "/contacts?organizationId={organizationId}";

    /**
     * Model-service path to create a new location
     */
    public static final String PATH_MODEL_LOCATION_CREATE = "/locations";

    /**
     * Model-service path to find contact by identifier
     */
    public static final String PATH_MODEL_LOCATION_FIND_BY_ID = "/locations/{locationId}";



    /**
     * Create absolute url to model-service. Host and port values are read from the application.properties.
     *
     * @param path resource path that is added to url after host and port values
     * @return absolute url to model-service resource
     */
    public String getUrl(String path) {
        return PATH_PREFIX + modelServiceHost + ":" + modelServicePort + path;
    }
}
