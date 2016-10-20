package fi.hel.allu.ui.mapper;

import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.domain.QueryParameterJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * Query parameter JSON mapping to search service model.
 */
public class QueryParameterMapper {

  private static final Logger logger = LoggerFactory.getLogger(QueryParameterMapper.class);

  public static QueryParameters mapToQueryParameters(QueryParametersJson queryParametersJson) {
    QueryParameters queryParameters = new QueryParameters();

    queryParameters.setQueryParameters(
        queryParametersJson.getQueryParameters()
            .stream().map(p -> mapToQueryParameter(p))
            .filter(p -> p != null)
            .collect(Collectors.toList()));

    if (queryParametersJson.getSort() != null) {
      queryParameters.setSort(
          new QueryParameters.Sort(
              queryParametersJson.getSort().field,
              QueryParameters.Sort.Direction.valueOf(queryParametersJson.getSort().direction.name())));
    }

    return queryParameters;
  }

  public static QueryParameter mapToQueryParameter(QueryParameterJson queryParameterJson) {
    if (queryParameterJson.getFieldValue() != null) {
      return new QueryParameter(queryParameterJson.getFieldName(), queryParameterJson.getFieldValue());
    } else if (queryParameterJson.getFieldMultiValue() != null) {
      return new QueryParameter(queryParameterJson.getFieldName(), queryParameterJson.getFieldMultiValue());
    } else if (queryParameterJson.getStartDateValue() != null || queryParameterJson.getEndDateValue() != null) {
      return new QueryParameter(
          queryParameterJson.getFieldName(), queryParameterJson.getStartDateValue(), queryParameterJson.getEndDateValue());
    } else {
      logger.warn("QueryParameter \"{}\" with no value skipped!", queryParameterJson.getFieldName());
      return null;
    }
  }
}
