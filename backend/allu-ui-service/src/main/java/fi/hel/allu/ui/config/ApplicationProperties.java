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
    public static final String PATH_MODEL_APPLICATION_CREATE = "/applications";
    public static final String PATH_MODEL_APPLICATION_FIND_BY_ID = "/applications/{applicationId}";
    public static final String PATH_MODEL_APPLICATION_FIND_BY_HANDLER = "/applications//byhandler/{handlerId}";
    public static final String PATH_MODEL_PERSON_CREATE = "/persons";
    public static final String PATH_MODEL_PERSON_FIND_BY_ID = "/persons/{personId}";
    public static final String PATH_MODEL_PROJECT_CREATE = "/projects";
    public static final String PATH_MODEL_PROJECT_FIND_BY_ID = "/projects/{projectId}";


    public String getUrl(String path)  {
        return PATH_PREFIX + modelServiceHost + ":" + modelServicePort + path;
    }
}
