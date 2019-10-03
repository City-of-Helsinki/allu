package fi.hel.allu.supervision.api.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.supervision.api.domain.ApplicationSearchParameterField;
import fi.hel.allu.supervision.api.domain.ApplicationSearchParameters;
import fi.hel.allu.supervision.api.domain.SearchParameterType;

public class ApplicationSearchParameterMapper {

  public static ApplicationQueryParameters mapToQueryParameters(ApplicationSearchParameters searchParameters) {
    ApplicationQueryParameters result = new ApplicationQueryParameters();
    List<QueryParameter> queryParams = new ArrayList<>();
    for (Entry<ApplicationSearchParameterField, String> parameter: searchParameters.getSearchParameters().entrySet()) {
      QueryParameter qp = mapQueryParameter(parameter.getKey(), parameter.getValue());
      queryParams.add(qp);
    }
    result.setQueryParameters(queryParams);
    result.setIntersectingGeometry(searchParameters.getIntersectingGeometry());
    return result;
  }

  public static ApplicationQueryParameters queryParametersForProject(Integer projectId) {
    ApplicationQueryParameters parameters = new ApplicationQueryParameters();
    parameters.setQueryParameters(Collections.singletonList(mapQueryParameter(ApplicationSearchParameterField.PROJECT_ID, projectId.toString())));
    return parameters;
  }

  public static QueryParameter mapQueryParameter(ApplicationSearchParameterField key, String value) {
    if (key.getType() == SearchParameterType.DATE) {
      return mapDateParameter(key, value);
    } else if (key.isMultiValue()) {
      return new QueryParameter(key.getSearchFieldName(), MapperUtil.split(value));
    } else {
      return new QueryParameter(key.getSearchFieldName(), value);
    }
  }


  private static QueryParameter mapDateParameter(ApplicationSearchParameterField key, String value) {
    if (key == ApplicationSearchParameterField.VALID_AFTER) {
      return new QueryParameter(key.getSearchFieldName(), MapperUtil.parseDate(value), null);
    } else {
      return new QueryParameter(key.getSearchFieldName(), null, MapperUtil.parseDate(value));
    }
  }

}
