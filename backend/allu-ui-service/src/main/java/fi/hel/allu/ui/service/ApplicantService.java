package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.PostalAddressJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicantJson;

@Service
public class ApplicantService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ApplicantService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;


  @Autowired
  public ApplicantService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
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
              .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICANT_CREATE), createApplicantModel(applicantJson),
          Applicant.class);
      mapApplicantToJson(applicantJson, applicantModel);
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
      restTemplate.put(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICANT_UPDATE), createApplicantModel(applicantJson),
          applicantJson.getId().intValue());
    }
  }

  public ApplicantJson findApplicantById(int applicantId) {
    ApplicantJson applicantJson = new ApplicantJson();
    ResponseEntity<Applicant> applicantResult = restTemplate.getForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICANT_FIND_BY_ID), Applicant.class, applicantId);
    mapApplicantToJson(applicantJson, applicantResult.getBody());

    return applicantJson;
  }


  private Applicant createApplicantModel(ApplicantJson applicantJson) {
    Applicant applicantModel = new Applicant();
    applicantModel.setId(applicantJson.getId());
    applicantModel.setType(applicantJson.getType());
    applicantModel.setName(applicantJson.getName());
    applicantModel.setRegistryKey(applicantJson.getRegistryKey());
    applicantModel.setPhone(applicantJson.getPhone());
    applicantModel.setEmail(applicantJson.getEmail());
    if (applicantJson.getPostalAddress() != null) {
      applicantModel.setStreetAddress(applicantJson.getPostalAddress().getStreetAddress());
      applicantModel.setCity(applicantJson.getPostalAddress().getCity());
      applicantModel.setPostalCode(applicantJson.getPostalAddress().getPostalCode());
    }
    return applicantModel;
  }

  private void mapApplicantToJson(ApplicantJson applicantJson, Applicant applicant) {
    applicantJson.setId(applicant.getId());
    applicantJson.setType(applicant.getType());
    applicantJson.setName(applicant.getName());
    applicantJson.setRegistryKey(applicant.getRegistryKey());
    applicantJson.setPhone(applicant.getPhone());
    applicantJson.setEmail(applicant.getEmail());
    PostalAddressJson postalAddressJson = null;
    if (applicant.getStreetAddress() != null || applicant.getCity() != null || applicant.getPostalCode() != null) {
      postalAddressJson = new PostalAddressJson();
      postalAddressJson.setStreetAddress(applicant.getStreetAddress());
      postalAddressJson.setCity(applicant.getCity());
      postalAddressJson.setPostalCode(applicant.getPostalCode());
    }
    applicantJson.setPostalAddress(postalAddressJson);
  }
}
