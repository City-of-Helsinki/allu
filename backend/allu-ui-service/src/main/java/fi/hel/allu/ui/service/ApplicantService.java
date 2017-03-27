package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicantService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;


  @Autowired
  public ApplicantService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
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
    // TODO: update ElasticSearch
    Applicant applicantModel = restTemplate.postForObject(
        applicationProperties.getApplicantCreateUrl(),
        applicationMapper.createApplicantModel(applicantJson),
        Applicant.class);
    return applicationMapper.createApplicantJson(applicantModel);
  }

  /**
   * Update the given applicant. Applicant is updated if the id is given.
   *
   * @param applicantJson applicant that is going to be updated
   */
  public ApplicantJson updateApplicant(int applicantId, ApplicantJson applicantJson) {
    // TODO: update ElasticSearch
    HttpEntity<Applicant> requestEntity = new HttpEntity<>(applicationMapper.createApplicantModel(applicantJson));
    ResponseEntity<Applicant> response = restTemplate.exchange(
        applicationProperties.getApplicantUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Applicant.class,
        applicantId);
    return applicationMapper.createApplicantJson(response.getBody());
  }

  public ApplicantJson findApplicantById(int applicantId) {
    ResponseEntity<Applicant> applicantResult =
        restTemplate.getForEntity(applicationProperties.getApplicantByIdUrl(), Applicant.class, applicantId);
    return applicationMapper.createApplicantJson(applicantResult.getBody());
  }

  public List<ApplicantJson> findAllApplicants() {
    ResponseEntity<Applicant[]> applicantResult =
        restTemplate.getForEntity(
            applicationProperties.getApplicantsUrl(),
            Applicant[].class);
    return Arrays.stream(applicantResult.getBody())
        .map(applicant -> applicationMapper.createApplicantJson(applicant))
        .collect(Collectors.toList());
  }
}
