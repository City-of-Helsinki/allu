package fi.hel.allu.search.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicantES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.GenericSearchService;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for searching and indexing applicants.
 */
@RestController
@RequestMapping("/applicants")
public class ApplicantController {

  private GenericSearchService applicantSearchService;
  private GenericSearchService applicationSearchService;

  @Autowired
  public ApplicantController(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client) {
    applicantSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.CUSTOMER_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICANT_TYPE_NAME);
    applicationSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICATION_TYPE_NAME);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody ApplicantES applicantES) {
    applicantSearchService.insert(applicantES.getId().toString(), applicantES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@RequestBody List<ApplicantES> applicantESs) {
    Map<String, Object> idToApplicant = applicantESs.stream().collect(Collectors.toMap(a -> a.getId().toString(), a -> a));
    applicantSearchService.bulkUpdate(idToApplicant);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/applications", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateApplicantOfApplications(
      @PathVariable String id,
      @RequestBody List<Integer> applicationIds) {
    ApplicantES applicantES = applicantSearchService.findObjectById(id, ApplicantES.class)
        .orElseThrow(() -> new NoSuchEntityException("No such applicant in ElasticSearch", id));
    Map<String, Object> idToApplicant = applicationIds.stream()
        .collect(Collectors.toMap(aid -> Integer.toString(aid), aid -> Collections.singletonMap("applicant", applicantES)));
    applicationSearchService.bulkUpdate(idToApplicant);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable String id) {
    applicantSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/index", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteIndex() {
    applicantSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<Integer>> search(@Valid @RequestBody QueryParameters queryParameters) {
    return new ResponseEntity<>(applicantSearchService.findByField(queryParameters), HttpStatus.OK);
  }

  // TODO: this method is not required, because partial searches may be done with QueryParameters too. Remove later
  @RequestMapping(value = "/search/{fieldName}", method = RequestMethod.POST)
  public ResponseEntity<List<Integer>> search(
      @PathVariable String fieldName,
      @RequestBody String searchString) {
    return new ResponseEntity<>(applicantSearchService.findPartial(fieldName, searchString), HttpStatus.OK);
  }
}
