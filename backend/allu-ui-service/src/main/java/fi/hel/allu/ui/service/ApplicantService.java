package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApplicantService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ApplicantService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;


  @Autowired
  public ApplicantService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      ApplicationMapper applicationMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
  }


  /**
   * Create a new applicant. Applicant id must null to create a new applicant.
   *
   * @param applicantJson Applicant that is going to be created
   * @return Created applicant
   */
  public ApplicantJson createApplicant(ApplicantJson applicantJson) {
    if (applicantJson != null && applicantJson.getId() == null) {
      Applicant applicantModel = restTemplate.postForObject(applicationProperties
          .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICANT_CREATE),
          applicationMapper.createApplicantModel(applicantJson),
          Applicant.class);
      applicationMapper.mapApplicantToJson(applicantJson, applicantModel);
    }
    return applicantJson;
  }

  /**
   * Update the given applicant. Applicant is updated if the id is given.
   *
   * @param applicantJson applicant that is going to be updated
   */
  public void updateApplicant(ApplicantJson applicantJson) {
    if (applicantJson != null && applicantJson.getId() != null && applicantJson.getId() > 0) {
      restTemplate.put(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICANT_UPDATE),
          applicationMapper.createApplicantModel(applicantJson),
          applicantJson.getId().intValue());
    }
  }

  public ApplicantJson findApplicantById(int applicantId) {
    ApplicantJson applicantJson = new ApplicantJson();
    ResponseEntity<Applicant> applicantResult = restTemplate.getForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICANT_FIND_BY_ID), Applicant.class, applicantId);
    applicationMapper.mapApplicantToJson(applicantJson, applicantResult.getBody());

    return applicantJson;
  }


}
