package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.domain.ApplicantWithContactsJson;
import fi.hel.allu.ui.domain.ContactJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import fi.hel.allu.ui.mapper.QueryParameterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicantService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;
  private SearchService searchService;
  private ContactService contactService;

  @Autowired
  public ApplicantService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      SearchService searchService,
      ContactService contactService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.searchService = searchService;
    this.contactService = contactService;
  }


  /**
   * Create a new applicant. Applicant id must null to create a new applicant.
   *
   * @param applicantJson Applicant that is going to be created
   * @return Created applicant
   */
  public ApplicantJson createApplicant(ApplicantJson applicantJson) {
    Applicant applicantModel = restTemplate.postForObject(
        applicationProperties.getApplicantCreateUrl(),
        applicationMapper.createApplicantModel(applicantJson),
        Applicant.class);
    ApplicantJson createdApplicant = applicationMapper.createApplicantJson(applicantModel);
    searchService.insertApplicant(createdApplicant);
    return createdApplicant;
  }

  /**
   * Create a new applicant with contacts. Applicant id must null to create a new applicant.
   *
   * @param applicantWithContactsJson Applicant with conctacts to be created.
   * @return Created applicant with contacts.
   */
  public ApplicantWithContactsJson createApplicantWithContacts(ApplicantWithContactsJson applicantWithContactsJson) {
    if (applicantWithContactsJson.getApplicant() == null) {
      throw new IllegalArgumentException("Applicant cannot be null when creating a new applicant!");
    }
    ApplicantJson applicantJson = createApplicant(applicantWithContactsJson.getApplicant());
    ApplicantWithContactsJson createdApplicantWithContacts = new ApplicantWithContactsJson();
    createdApplicantWithContacts.setApplicant(applicantJson);

    if (applicantWithContactsJson.getContacts() != null) {
      List<ContactJson> newContacts = applicantWithContactsJson.getContacts();
      newContacts.forEach(c -> c.setApplicantId(applicantJson.getId()));
      List<ContactJson> contacts = contactService.createContacts(applicantWithContactsJson.getContacts());
      searchService.insertContacts(contacts);
      createdApplicantWithContacts.setContacts(contacts);
    }

    return createdApplicantWithContacts;
  }

  /**
   * Update the given applicant. Applicant is updated if the id is given.
   *
   * @param applicantJson applicant that is going to be updated
   */
  public ApplicantJson updateApplicant(int applicantId, ApplicantJson applicantJson) {
    HttpEntity<Applicant> requestEntity = new HttpEntity<>(applicationMapper.createApplicantModel(applicantJson));
    ResponseEntity<Applicant> response = restTemplate.exchange(
        applicationProperties.getApplicantUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Applicant.class,
        applicantId);
    ApplicantJson updatedApplicant = applicationMapper.createApplicantJson(response.getBody());
    searchService.updateApplicants(Collections.singletonList(updatedApplicant));
    // update search index of applications having this applicant
    ResponseEntity<Integer[]> applicationIdResult =
        restTemplate.getForEntity(
            applicationProperties.getApplicantApplicationsUrl(),
            Integer[].class,
            applicantId);
    if (applicationIdResult.getBody().length != 0) {
      searchService.updateApplicantOfApplications(updatedApplicant, Arrays.asList(applicationIdResult.getBody()));
    }

    return updatedApplicant;
  }

  public ApplicantWithContactsJson updateApplicantWithContacts(ApplicantWithContactsJson applicantWithContactsJson) {
    ApplicantWithContactsJson updatedApplicantWithContactsJson = new ApplicantWithContactsJson();
    if (applicantWithContactsJson.getApplicant() != null) {
      ApplicantJson updatedApplicant = applicantWithContactsJson.getApplicant();
      updatedApplicantWithContactsJson.setApplicant(updateApplicant(updatedApplicant.getId(), updatedApplicant));
    }
    if (applicantWithContactsJson.getContacts() != null) {
      Map<Boolean, List<ContactJson>> newOldContacts =
          applicantWithContactsJson.getContacts().stream().collect(Collectors.partitioningBy(c -> c.getId() != null));
      ArrayList<ContactJson> allContacts = new ArrayList<>();
      if (!newOldContacts.get(false).isEmpty()) {
        List<ContactJson> newContacts = contactService.createContacts(newOldContacts.get(false));
        searchService.insertContacts(newContacts);
        allContacts.addAll(newContacts);
      }
      if (!newOldContacts.get(true).isEmpty()) {
        List<ContactJson> oldContacts = contactService.updateContacts(newOldContacts.get(true));
        searchService.updateContacts(oldContacts);
        allContacts.addAll(oldContacts);
      }
      updatedApplicantWithContactsJson.setContacts(allContacts);
    }

    return updatedApplicantWithContactsJson;
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

  /**
   * Find applicants by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public List<ApplicantJson> search(QueryParametersJson queryParameters) {
    List<ApplicantJson> resultList = Collections.emptyList();
    if (!queryParameters.getQueryParameters().isEmpty()) {
      List<Integer> ids = searchService.searchApplicant(QueryParameterMapper.mapToQueryParameters(queryParameters));
      resultList = getApplicantsById(ids);
    }
    return resultList;
  }

  /**
   * Search applicants with partial search.
   *
   * @param fieldName     Field name that should match the search string.
   * @param searchString  Search string.
   */
  public List<ApplicantJson> searchPartial(String fieldName, String searchString) {
    List<Integer> applicantIds = searchService.searchApplicantPartial(fieldName, searchString);
    return getApplicantsById(applicantIds);
  }

  private List<ApplicantJson> getApplicantsById(List<Integer> applicantIds) {
    Applicant[] applicants = restTemplate.postForObject(
        applicationProperties.getApplicantsByIdUrl(),
        applicantIds,
        Applicant[].class);
    List<ApplicantJson> resultList = Arrays.asList(applicants).stream().map(a -> applicationMapper.createApplicantJson(a)).collect(Collectors.toList());
    SearchService.orderByIdList(applicantIds, resultList, (applicant) -> applicant.getId());
    return resultList;
  }
}
