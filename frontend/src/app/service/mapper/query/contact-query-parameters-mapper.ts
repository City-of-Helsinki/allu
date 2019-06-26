import {BackendQueryParameter, BackendQueryParameters} from '@service/backend-model/backend-query-parameters';
import {QueryParametersMapper} from '@service/mapper/query/query-parameters-mapper';
import {ContactSearchQuery} from '@service/customer/contact-search-query';

export class ContactQueryParametersMapper {
  public static mapFrontend(query: ContactSearchQuery): BackendQueryParameters {
    return {
      queryParameters: ContactQueryParametersMapper.mapParameters(query)
    };
  }

  private static mapParameters(query: ContactSearchQuery): Array<BackendQueryParameter> {
    const queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(queryParameters, 'name', QueryParametersMapper.removeExtraWhitespace(query.name));
    QueryParametersMapper.mapBooleanParameter(queryParameters, 'active', query.active);
    return queryParameters;
  }
}
