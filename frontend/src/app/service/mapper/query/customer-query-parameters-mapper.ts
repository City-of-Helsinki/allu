import {BackendQueryParameter, BackendQueryParameters} from '../../backend-model/backend-query-parameters';
import {QueryParametersMapper} from './query-parameters-mapper';
import {CustomerSearchQuery} from '../../customer/customer-search-query';

export class CustomerQueryParametersMapper {
  public static mapFrontend(query: CustomerSearchQuery): BackendQueryParameters {
    return query
      ? { queryParameters: CustomerQueryParametersMapper.mapCustomerParameters(query) }
      : undefined;
  }

  private static mapCustomerParameters(query: CustomerSearchQuery): Array<BackendQueryParameter> {
    const queryParameters: Array<BackendQueryParameter> = [];
    // prioritize search parameters by boosting the query parameters
    QueryParametersMapper.mapParameter(queryParameters,
      'name', QueryParametersMapper.removeExtraWhitespace(query.name), 1);
    QueryParametersMapper.mapParameter(queryParameters,
      'registryKey', QueryParametersMapper.removeExtraWhitespace(query.registryKey), 2);
    QueryParametersMapper.mapParameter(queryParameters,
      'sapCustomerNumber', QueryParametersMapper.removeExtraWhitespace(query.sapCustomerNumber), 4);

    QueryParametersMapper.mapRawParameter(queryParameters, 'type', query.type);
    QueryParametersMapper.mapBooleanParameter(queryParameters, 'active', query.active);
    QueryParametersMapper.mapBooleanParameter(queryParameters, 'invoicingOnly', query.invoicingOnly);
    return queryParameters;
  }
}
