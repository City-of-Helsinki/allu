package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.ApplicationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Value("${backend.application.create.url}")
    private String createApplicationUrl;

    @Value("${backend.application.findbyid.url}")
    private String findByIdApplicationUrl;

    @Autowired
    private RestTemplate restTemplate;


    /**
     * Create new application by calling backend service.
     *
     * @param applications List of applications that are going to be created
     * @return List of created applications and their identifiers
     */
    public ApplicationDTO createApplication(ApplicationDTO applications) {
        return restTemplate.postForObject(createApplicationUrl, applications, ApplicationDTO.class);
    }


    /**
     * Find application details.
     *
     * @param applicationId application identifier that is used to find details
     * @return Application details or empty DTO
     */
    public ApplicationDTO findApplicationById(String applicationId) {
        return restTemplate.getForObject(findByIdApplicationUrl, ApplicationDTO.class, applicationId);
    }
}
