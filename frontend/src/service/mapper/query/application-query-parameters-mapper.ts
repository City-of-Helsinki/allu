import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {BackendQueryParameter, BackendQueryParameters} from '../../backend-model/backend-query-parameters';
import {END_TIME_FIELD, QueryParametersMapper, START_TIME_FIELD} from './query-parameters-mapper';
import {MAX_DATE, MIN_DATE} from '../../../util/time.util';
import {Some} from '../../../util/option';

export class ApplicationQueryParametersMapper {
  public static mapFrontend(query: ApplicationSearchQuery): BackendQueryParameters {
    return (query) ?
      {
        queryParameters: ApplicationQueryParametersMapper.mapApplicationParameters(query),
        sort: QueryParametersMapper.mapSort(query)
      } : undefined;
  }

  private static mapApplicationParameters(query: ApplicationSearchQuery): Array<BackendQueryParameter> {
    let queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(
      queryParameters, 'locations.streetAddress', QueryParametersMapper.removeExtraWhitespace(query.address));
    QueryParametersMapper.mapParameter(queryParameters, 'applicant.name', QueryParametersMapper.removeExtraWhitespace(query.applicant));
    QueryParametersMapper.mapParameter(queryParameters, 'contacts.name', QueryParametersMapper.removeExtraWhitespace(query.contact));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'handler.userName', query.handler);
    QueryParametersMapper.mapParameter(queryParameters, 'applicationId', QueryParametersMapper.removeExtraWhitespace(query.applicationId));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'status', query.status);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'locations.cityDistrictId', query.districts);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'applicationTags', query.tags);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'type', query.type);
    QueryParametersMapper.mapParameter(queryParameters, '_all', query.freeText);
    QueryParametersMapper.mapDateParameter(queryParameters, 'recurringApplication', query.startTime, query.endTime);
    Some(query.projectId).do(projectId => QueryParametersMapper.mapParameter(queryParameters, 'projectId', projectId.toString()));
    return queryParameters;
  }
}
