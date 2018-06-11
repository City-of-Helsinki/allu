import {ProjectSearchQuery} from '../../../model/project/project-search-query';
import {BackendQueryParameter, BackendQueryParameters} from '../../backend-model/backend-query-parameters';
import {Some} from '../../../util/option';
import {END_TIME_FIELD, QueryParametersMapper, START_TIME_FIELD} from './query-parameters-mapper';
import {MAX_DATE, MIN_DATE} from '../../../util/time.util';
export class ProjectQueryParametersMapper {

  public static mapFrontend(query: ProjectSearchQuery): BackendQueryParameters {
    return Some(query).map(q => {
      return {
        queryParameters: ProjectQueryParametersMapper.mapProjectParameters(query)
      };
    }).orElse(undefined);
  }

  private static mapProjectParameters(query: ProjectSearchQuery): Array<BackendQueryParameter> {
    const queryParameters: Array<BackendQueryParameter> = [];
    Some(query.id).do(id => QueryParametersMapper.mapParameter(queryParameters, 'id', id.toString()));
    Some(query.identifier).do(identifier => QueryParametersMapper.mapParameter(queryParameters, 'identifier', identifier));
    QueryParametersMapper.mapDateParameter(queryParameters, START_TIME_FIELD, MIN_DATE, query.endTime);
    QueryParametersMapper.mapDateParameter(queryParameters, END_TIME_FIELD, query.startTime, MAX_DATE);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'cityDistricts', query.districts);
    QueryParametersMapper.mapParameter(queryParameters, 'ownerName', QueryParametersMapper.removeExtraWhitespace(query.ownerName));
    Some(query.onlyActive).do(onlyActive => ProjectQueryParametersMapper.mapProjectActivityParameter(queryParameters, onlyActive));
    Some(query.creator).do(creator => QueryParametersMapper.mapParameter(queryParameters, 'creator', creator.toString()));
    return queryParameters;
  }


  private static mapProjectActivityParameter(
    queryParameters: Array<BackendQueryParameter>,
    onlyActive: boolean) {
    if (onlyActive) {
      const startTimeParameter = QueryParametersMapper.createDateParameter(START_TIME_FIELD, MIN_DATE, new Date());
      const endTimeParameter = QueryParametersMapper.createDateParameter(END_TIME_FIELD, new Date(), MAX_DATE);
      queryParameters.push(startTimeParameter);
      queryParameters.push(endTimeParameter);
    }
  }
}
