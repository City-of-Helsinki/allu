package fi.hel.allu.supervision.api.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.supervision.api.domain.SearchField;
import fi.hel.allu.supervision.api.domain.SearchParameters;

public class CustomerSearchParameterMapper {

  public static <S extends SearchField, T extends SearchParameters<S>> QueryParameters mapToQueryParameters(T searchParameters) {
    QueryParameters result = new QueryParameters();
    List<QueryParameter> queryParams = new ArrayList<>();
    for (Entry<S, String> parameter: searchParameters.getSearchParameters().entrySet()) {
      QueryParameter qp = mapQueryParameter(parameter.getKey(), parameter.getValue());
      queryParams.add(qp);
    }
    result.setQueryParameters(queryParams);
    return result;
  }

  public static <S extends SearchField> QueryParameter mapQueryParameter(S key, String value) {
    return new QueryParameter(key.getSearchFieldName(), value);
  }

}
