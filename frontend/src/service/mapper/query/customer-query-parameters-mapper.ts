import {BackendQueryParameter, BackendQueryParameters} from '../../backend-model/backend-query-parameters';
import {QueryParametersMapper} from './query-parameters-mapper';
import {Sort} from '../../../model/common/sort';

export class CustomerQueryParametersMapper {
  public static mapFrontend(query: CustomerSearchQuery): BackendQueryParameters {
    return {
      queryParameters: CustomerQueryParametersMapper.mapCustomerParameters(query),
      sort: QueryParametersMapper.mapSort(query)
    };
  }

  private static mapCustomerParameters(query: CustomerSearchQuery): Array<BackendQueryParameter> {
    let queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(queryParameters, 'name', QueryParametersMapper.removeExtraWhitespace(query.name));
    QueryParametersMapper.mapRawParameter(queryParameters, 'type', query.type);
    QueryParametersMapper.mapBooleanParameter(queryParameters, 'active', query.active);
    return queryParameters;
  }
}

export interface CustomerSearchQuery {
  name?: string;
  type?: string;
  active?: boolean;
  sort?: Sort;
}
