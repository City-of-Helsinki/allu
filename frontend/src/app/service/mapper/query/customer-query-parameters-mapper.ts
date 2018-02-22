import {BackendQueryParameter, BackendQueryParameters} from '../../backend-model/backend-query-parameters';
import {QueryParametersMapper} from './query-parameters-mapper';
import {CustomerSearchQuery} from '../../customer/customer-search-query';

export class CustomerQueryParametersMapper {
  public static mapFrontend(query: CustomerSearchQuery): BackendQueryParameters {
    return {
      queryParameters: CustomerQueryParametersMapper.mapCustomerParameters(query)
    };
  }

  private static mapCustomerParameters(query: CustomerSearchQuery): Array<BackendQueryParameter> {
    const queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(queryParameters, 'name', QueryParametersMapper.removeExtraWhitespace(query.name));
    QueryParametersMapper.mapParameter(queryParameters, 'registryKey', QueryParametersMapper.removeExtraWhitespace(query.registryKey));
    QueryParametersMapper.mapRawParameter(queryParameters, 'type', query.type);
    QueryParametersMapper.mapBooleanParameter(queryParameters, 'active', query.active);
    QueryParametersMapper.mapBooleanParameter(queryParameters, 'invoicingOnly', query.invoicingOnly);
    return queryParameters;
  }
}
