package fi.hel.allu.supervision.api.mapper;

import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.supervision.api.domain.CustomerSearchParameterField;
import fi.hel.allu.supervision.api.domain.CustomerSearchParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class CustomerSearchParameterMapper {

  public static QueryParameters mapToQueryParameters(CustomerSearchParameters searchParameters) {
    QueryParameters result = new QueryParameters();
    List<QueryParameter> queryParams = new ArrayList<>();
    for (Entry<CustomerSearchParameterField, String> parameter: searchParameters.getSearchParameters().entrySet()) {
      QueryParameter qp = mapQueryParameter(parameter.getKey(), parameter.getValue());
      queryParams.add(qp);
    }
    result.setQueryParameters(queryParams);
    return result;
  }

  public static QueryParameter mapQueryParameter(CustomerSearchParameterField key, String value) {
    return new QueryParameter(key.getSearchFieldName(), value);
  }

}
