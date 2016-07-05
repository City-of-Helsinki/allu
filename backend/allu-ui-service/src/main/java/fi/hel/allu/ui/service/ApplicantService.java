package fi.hel.allu.ui.service;

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
  private PersonService personService;
  private OrganizationService organizationService;


  @Autowired
  public ApplicantService(ApplicationProperties applicationProperties, RestTemplate restTemplate, PersonService personService,
                          OrganizationService organizationService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.personService = personService;
    this.organizationService = organizationService;
  }


  /**
   * Create a new applicant. Applicant id must null to create a new applicant.
   *
   * @param applicantJson Applicant that is going to be created
   * @return Created applicant
   */
  public ApplicantJson createApplicant(ApplicantJson applicantJson) {
    if (applicantJson != null && applicantJson.getId() == null) {
      switch (applicantJson.getType()) {
        case Person:
          applicantJson.setPerson(personService.createPerson(applicantJson.getPerson()));
          break;
        case Company:
      case Association:
          applicantJson.setOrganization(organizationService.createOrganization(applicantJson.getOrganization()));
          break;
      }
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
      switch (applicantJson.getType()) {
        case Person:
          personService.updatePerson(applicantJson.getPerson());
          break;
        case Company:
      case Association:
          organizationService.updateOrganization(applicantJson.getOrganization());
          break;
      }
      restTemplate.put(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICANT_UPDATE), createApplicantModel(applicantJson),
          applicantJson.getId().intValue());
    }
  }

  public ApplicantJson findApplicantById(int applicantId) {
    ApplicantJson applicantJson = new ApplicantJson();
    ResponseEntity<Applicant> applicantResult = restTemplate.getForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICANT_FIND_BY_ID), Applicant.class, applicantId);
    mapApplicantToJson(applicantJson, applicantResult.getBody());

    switch (applicantResult.getBody().getType()) {
      case Person:
        applicantJson.setPerson(personService.findPersonById(applicantResult.getBody().getPersonId()));
        break;
      case Company:
    case Association:
        applicantJson.setOrganization(organizationService.findOrganizationById(applicantResult.getBody().getOrganizationId()));
        break;
    }
    return applicantJson;
  }


  private Applicant createApplicantModel(ApplicantJson applicantJson) {
    Applicant applicantModel = new Applicant();
    if (applicantJson.getId() != null) {
      applicantModel.setId(applicantJson.getId());
    }
    if (applicantJson.getPerson() != null) {
      applicantModel.setPersonId(applicantJson.getPerson().getId());
    }
    if (applicantJson.getOrganization() != null) {
      applicantModel.setOrganizationId(applicantJson.getOrganization().getId());
    }
    applicantModel.setType(applicantJson.getType());
    return applicantModel;
  }

  private void mapApplicantToJson(ApplicantJson applicantJson, Applicant applicant) {
    applicantJson.setId(applicant.getId());
    applicantJson.setType(applicant.getType());
  }
}
