import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {BackendQueryParameter, BackendQueryParameters} from '@service/backend-model/backend-query-parameters';
import {QueryParametersMapper} from './query-parameters-mapper';
import {Some} from '@util/option';

export class ApplicationQueryParametersMapper {
  public static mapFrontend(query: ApplicationSearchQuery): BackendQueryParameters {
    return (query) ?
      {
        queryParameters: ApplicationQueryParametersMapper.mapApplicationParameters(query)
      } : undefined;
  }

  private static mapApplicationParameters(query: ApplicationSearchQuery): Array<BackendQueryParameter> {
    const queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(
      queryParameters, 'locations.address', QueryParametersMapper.removeExtraWhitespace(query.address));
    QueryParametersMapper.mapParameter(queryParameters, 'customers.applicant.customer.name',
      QueryParametersMapper.removeExtraWhitespace(query.applicant));
    QueryParametersMapper.mapParameter(queryParameters, 'customers.applicant.contacts.name',
      QueryParametersMapper.removeExtraWhitespace(query.contact));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'owner.userName', query.owner);
    QueryParametersMapper.mapParameter(queryParameters, 'applicationId', QueryParametersMapper.removeExtraWhitespace(query.applicationId));
    QueryParametersMapper.mapParameter(queryParameters, 'name', QueryParametersMapper.removeExtraWhitespace(query.name));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'status', query.status);
    const districts = query.districts || [];
    QueryParametersMapper.mapArrayParameter(queryParameters, 'locations.cityDistrictId', districts.map(id => id.toString()));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'applicationTags', query.tags);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'type', query.type);
    QueryParametersMapper.mapParameter(queryParameters, '_all', query.freeText);
    QueryParametersMapper.mapDateParameter(queryParameters, 'recurringApplication', query.startTime, query.endTime, true);
    Some(query.projectId).do(projectId => QueryParametersMapper.mapParameter(queryParameters, 'projectId', projectId.toString()));
    return queryParameters;
  }
}
