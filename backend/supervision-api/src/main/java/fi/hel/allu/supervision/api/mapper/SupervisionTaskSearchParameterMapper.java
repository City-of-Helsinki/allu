package fi.hel.allu.supervision.api.mapper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.domain.ApplicationSearchParameterField;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchParameterField;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchParameters;

@Component
public class SupervisionTaskSearchParameterMapper {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private UserService userService;

  public SupervisionTaskSearchCriteria createSearchCriteria(SupervisionTaskSearchParameters searchParameters) {
    SupervisionTaskSearchCriteria criteria = new SupervisionTaskSearchCriteria();
    ApplicationQueryParameters applicationQueryParameters = new ApplicationQueryParameters();
    applicationQueryParameters.setIntersectingGeometry(searchParameters.getIntersectingGeometry());
    for (Entry<SupervisionTaskSearchParameterField, String> parameter : searchParameters.getSearchParameters().entrySet()) {
      switch (parameter.getKey()) {
      case APPLICATION_IDENTIFIER:
        applicationQueryParameters.setQueryParameters(Collections.singletonList(ApplicationSearchParameterMapper
            .mapQueryParameter(ApplicationSearchParameterField.APPLICATION_IDENTIFIER, parameter.getValue())));
        break;
      case OWNER_USERNAME:
        criteria.setOwners(fetchUserIds(parameter.getValue()));
        break;
      case OWNER_ID:
        criteria.setOwners(MapperUtil.split(parameter.getValue()).stream().map(s -> Integer.valueOf(s)).collect(Collectors.toList()));
        break;
      case STATUS:
        criteria.setStatuses(toStatuses(parameter.getValue()));
        break;
      case TYPE:
        criteria.setTaskTypes(toTaskTypes(parameter.getValue()));
        break;
      case VALID_AFTER:
        criteria.setAfter(MapperUtil.parseDate(parameter.getValue()));
        break;
      case VALID_BEFORE:
        criteria.setBefore(MapperUtil.parseDate(parameter.getValue()));
        break;
      }
    }
    if (!applicationQueryParameters.getQueryParameters().isEmpty() || applicationQueryParameters.getIntersectingGeometry() != null) {
      List<Integer> applicationIds = fetchApplicationIds(applicationQueryParameters);
      criteria.setApplicationIds(applicationIds);
    }
    return criteria;
  }

  private List<Integer> fetchApplicationIds(ApplicationQueryParameters applicationQueryParameters) {
    PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE, null);
    Page<ApplicationES> result = applicationServiceComposer.search(applicationQueryParameters, pageRequest, Boolean.FALSE);
    return result.getContent().stream().map(ApplicationES::getId).collect(Collectors.toList());
  }

  private List<SupervisionTaskType> toTaskTypes(String value) {
    return MapperUtil.split(value).stream().map(v -> SupervisionTaskType.valueOf(v)).collect(Collectors.toList());
  }

  private List<SupervisionTaskStatusType> toStatuses(String value) {
    return MapperUtil.split(value).stream().map(v -> SupervisionTaskStatusType.valueOf(v)).collect(Collectors.toList());
  }

  private List<Integer> fetchUserIds(String value) {
    return MapperUtil.split(value).stream().map(v -> userService.findUserByUserName(v)).map(UserJson::getId)
        .collect(Collectors.toList());
  }


}
