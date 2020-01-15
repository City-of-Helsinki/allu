package fi.hel.allu.external.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.DocumentSearchResult;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.external.domain.ApprovalDocumentSearchResult;
import fi.hel.allu.external.domain.DecisionSearchResult;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.domain.LocationES;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.UserService;

@Component
public class DocumentSearchResultMapper {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private UserService userService;


  public List<DecisionSearchResult> mapToDecisionSearchResults(List<DocumentSearchResult> searchResults) {
    Map<Integer, ApplicationES> applicationsById = findApplications(searchResults);
    Map<Integer, UserJson> usersById = findUsers(searchResults);
    return searchResults.stream()
        .map(sr -> mapToDecisionSearchResult(sr, applicationsById.get(sr.getId()), usersById.get(sr.getDecisionMakerId())))
        .collect(Collectors.toList());
  }

  private DecisionSearchResult mapToDecisionSearchResult(DocumentSearchResult searchResult, ApplicationES application, UserJson decisionMaker) {
    return new DecisionSearchResult(
        searchResult.getId(),
        Optional.ofNullable(application).map(ApplicationES::getApplicationId).orElse(null),
        Optional.ofNullable(application).map(a -> getAddress(a)).orElse(null),
        Optional.ofNullable(decisionMaker).map(UserJson::getRealName).orElse(null),
        searchResult.getDocumentDate());
  }

  public List<ApprovalDocumentSearchResult> mapApprovalDocumentSearchResults(List<DocumentSearchResult> searchResults, ApprovalDocumentType type) {
    Map<Integer, ApplicationES> applicationsById = findApplications(searchResults);
    return searchResults.stream()
        .map(sr -> mapToApprovalDocumentSearchResult(sr, applicationsById.get(sr.getId()), type))
        .collect(Collectors.toList());
  }

  private ApprovalDocumentSearchResult mapToApprovalDocumentSearchResult(DocumentSearchResult searchResult, ApplicationES application, ApprovalDocumentType type) {
    return new ApprovalDocumentSearchResult(
        searchResult.getId(),
        Optional.ofNullable(application).map(ApplicationES::getApplicationId).orElse(null),
        Optional.ofNullable(application).map(a -> getAddress(a)).orElse(null),
        type,
        searchResult.getDocumentDate());
  }

  private String getAddress(ApplicationES application) {
    return application.getLocations().stream()
        .map(LocationES::getAddress)
        .collect(Collectors.joining(", "));
  }

  private Map<Integer, UserJson> findUsers(List<DocumentSearchResult> searchResults) {
    return searchResults.stream()
        .map(sr -> sr.getDecisionMakerId())
        .filter(userId -> userId != null)
        .distinct()
        .map(userId -> userService.findUserById(userId))
        .collect(Collectors.toMap(UserJson::getId, Function.identity()));
  }

  private Map<Integer, ApplicationES> findApplications(List<DocumentSearchResult> searchResults) {
    List<String> applicationIds = searchResults.stream()
        .map(sr -> sr.getId().toString())
        .collect(Collectors.toList());
    if (applicationIds.isEmpty()) {
      return Collections.emptyMap();
    }
    PageRequest pageRequest = PageRequest.of(0, applicationIds.size());
    ApplicationQueryParameters parameters = new ApplicationQueryParameters();
    parameters.setQueryParameters(Collections.singletonList(new QueryParameter("id", applicationIds)));
    parameters.setIncludeArchived(true);
    return applicationServiceComposer.search(parameters, pageRequest, false).getContent()
      .stream()
      .collect(Collectors.toMap(ApplicationES::getId, Function.identity()));
  }
}
