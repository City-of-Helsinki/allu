package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.QueryParameterJson;
import fi.hel.allu.servicecore.domain.QueryParametersJson;
import fi.hel.allu.servicecore.domain.UserJson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Services needed by workqueue.
 */
@Service
public class WorkQueueService {

  private static final Map<RoleType, List<String>> roleTypeToAllowedStatus;
  private ApplicationServiceComposer applicationServiceComposer;
  private UserService userService;

  static {
    roleTypeToAllowedStatus = new HashMap<>();
    // populate with empty lists to avoid boring problems with null return values
    Arrays.stream(RoleType.values()).forEach(rt -> roleTypeToAllowedStatus.put(rt, Collections.emptyList()));
    roleTypeToAllowedStatus.put(RoleType.ROLE_PROCESS_APPLICATION, Arrays.asList(
        StatusType.PRE_RESERVED.name(),
        StatusType.PENDING.name(),
        StatusType.HANDLING.name(),
        StatusType.RETURNED_TO_PREPARATION.name()));
    roleTypeToAllowedStatus.put(RoleType.ROLE_DECISION, Arrays.asList(StatusType.DECISIONMAKING.name()));
    roleTypeToAllowedStatus.put(RoleType.ROLE_SUPERVISE, Arrays.asList(StatusType.HANDLING.name(), StatusType.DECISION.name()));
  }

  @Autowired
  public WorkQueueService(ApplicationServiceComposer applicationServiceComposer, UserService userService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.userService = userService;
  }

  /**
   * Search that adds default search filtering according to user role and application type settings.
   *
   * @param   queryParametersJson   Original query, which will get user specific filtering added.
   * @return  List of applications matching the given search query.
   */
  public Page<ApplicationJson> searchSharedByGroup(QueryParametersJson queryParametersJson, Pageable pageRequest) {
    // find application type and status query parameters, if any
    Map<Boolean, List<QueryParameterJson>> partitionedByType =
        partitionByField(queryParametersJson.getQueryParameters(), QueryParameter.FIELD_NAME_APPLICATION_TYPE);
    Map<Boolean, List<QueryParameterJson>> partitionedByStatus =
        partitionByField(partitionedByType.get(Boolean.FALSE), QueryParameter.FIELD_NAME_STATUS);
    QueryParameterJson applicationTypeParameter =
        getOrCreateParameter(partitionedByType.get(Boolean.TRUE), QueryParameter.FIELD_NAME_APPLICATION_TYPE);
    QueryParameterJson statusParameter = getOrCreateParameter(partitionedByStatus.get(Boolean.TRUE), QueryParameter.FIELD_NAME_STATUS);
    List<QueryParameterJson> otherParameters = partitionedByStatus.get(Boolean.FALSE);

    // merge given parameters with the ones extracted from user information
    UserJson user = userService.getCurrentUser();

    applicationTypeParameter.setFieldMultiValue(
        mergeLists(
            user.getAllowedApplicationTypes().stream().map(at -> at.name()).collect(Collectors.toList()),
            applicationTypeParameter.getFieldMultiValue()));
    statusParameter.setFieldMultiValue(
        mergeLists(
            user.getAssignedRoles().stream().flatMap(role -> roleTypeToAllowedStatus.get(role).stream()).collect(Collectors.toList()),
            statusParameter.getFieldMultiValue()));
    queryParametersJson.setQueryParameters(
        mergeLists(
            otherParameters,
            Arrays.asList(applicationTypeParameter, statusParameter)));

    return applicationServiceComposer.search(queryParametersJson, pageRequest, false);
  }

  private Map<Boolean, List<QueryParameterJson>> partitionByField(List<QueryParameterJson> queryParameters, String fieldName) {
    return queryParameters.stream().collect(Collectors.partitioningBy(qp -> qp.getFieldName().equals(fieldName)));
  }

  private QueryParameterJson getOrCreateParameter(List<QueryParameterJson> queryParameters, String fieldName) {
    return queryParameters.stream().filter(qp -> qp.getFieldName().equals(fieldName)).findFirst()
        .orElse(new QueryParameterJson(fieldName, Collections.emptyList()));
  }

  private <T> List<T> mergeLists(List<T> a, List<T> b) {
    Set<T> merged = new HashSet<>(a);
    merged.addAll(b);
    return new ArrayList<>(merged);
  }
}
