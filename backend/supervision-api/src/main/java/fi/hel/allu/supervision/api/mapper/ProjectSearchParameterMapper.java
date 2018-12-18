package fi.hel.allu.supervision.api.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.supervision.api.domain.ProjectSearchParameterField;
import fi.hel.allu.supervision.api.domain.ProjectSearchParameters;

@Component
public class ProjectSearchParameterMapper {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  public QueryParameters mapToQueryParameters(ProjectSearchParameters searchParameters) {
    QueryParameters result = new QueryParameters();
    List<QueryParameter> queryParams = new ArrayList<>();
    Map<ProjectSearchParameterField, String> params = searchParameters.getSearchParameters();
    String applicationIdentifier = params.remove(ProjectSearchParameterField.APPLICATION_IDENTIFIER);
    for (Entry<ProjectSearchParameterField, String> parameter: params.entrySet()) {
      queryParams.add(mapToQueryParameter(parameter.getKey(), parameter.getValue()));
    }
    addApplicationSearchParameters(searchParameters, queryParams, applicationIdentifier);
    result.setQueryParameters(queryParams);
    return result;
  }

  private void addApplicationSearchParameters(ProjectSearchParameters searchParameters,
      List<QueryParameter> queryParams, String applicationIdentifiers) {
    if (StringUtils.isNotBlank(applicationIdentifiers) || searchParameters.getIntersectingGeometry() != null) {
      List<Integer> projectIds = getProjectIdsFromApplications(applicationIdentifiers, searchParameters.getIntersectingGeometry());
      if (projectIds.isEmpty()) {
        // No projects found with application search parameters, add -1 to return no results
        projectIds.add(-1);
      }
      queryParams.add(new QueryParameter("id", projectIds.stream().map(i -> i.toString()).collect(Collectors.toList())));
    }
  }

  private List<Integer> getProjectIdsFromApplications(String applicationIdentifier, Geometry intersectingGeometry) {
    ApplicationQueryParameters applicationQueryParameters = new ApplicationQueryParameters();
    if (StringUtils.isNotBlank(applicationIdentifier)) {
      applicationQueryParameters.setQueryParameters(Collections.singletonList(new QueryParameter("applicationId", applicationIdentifier)));
    }
    applicationQueryParameters.setIntersectingGeometry(intersectingGeometry);
    applicationQueryParameters.setHasProject(true);
    PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE, null);
    Page<ApplicationES> result = applicationServiceComposer.search(applicationQueryParameters, pageRequest, Boolean.FALSE);
    return result.getContent().stream().filter(a -> a.getProject() != null).map(a -> a.getProject().getId())
        .collect(Collectors.toList());
   }

  private QueryParameter mapToQueryParameter(ProjectSearchParameterField key, String value) {
    if (key == ProjectSearchParameterField.VALID_AFTER) {
      return new QueryParameter(key.getSearchFieldName(), MapperUtil.parseDate(value), null);
    } else if (key == ProjectSearchParameterField.VALID_BEFORE) {
      return new QueryParameter(key.getSearchFieldName(), null, MapperUtil.parseDate(value));
    } else {
      return new QueryParameter(key.getSearchFieldName(), value);
    }
  }
}
