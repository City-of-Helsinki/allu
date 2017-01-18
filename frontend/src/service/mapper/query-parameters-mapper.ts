import {BackendQueryParameters, BackendQueryParameter, BackendQuerySort} from '../backend-model/backend-query-parameters';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {MAX_DATE, MIN_DATE} from '../../util/time.util';
import {Direction} from '../../model/common/sort';
import {ProjectSearchQuery} from '../../model/project/project-search-query';
import {Some} from '../../util/option';
import {SearchQuery} from '../../model/common/search-query';

const enumFields = [
  'status',
  'type'
];

const START_TIME_FIELD = 'startTime';
const END_TIME_FIELD = 'endTime';

export class QueryParametersMapper {
  public static mapApplicationQueryFrontend(query: ApplicationSearchQuery): BackendQueryParameters {
    return (query) ?
    {
      queryParameters: QueryParametersMapper.mapApplicationParameters(query),
      sort: QueryParametersMapper.mapSort(query)
    } : undefined;
  }

  public static mapProjectQueryFrontend(query: ProjectSearchQuery): BackendQueryParameters {
    return Some(query).map(q => {
      return {
        queryParameters: QueryParametersMapper.mapProjectParameters(query),
        sort: QueryParametersMapper.mapSort(query)
      };
    }).orElse(undefined);
  }

  private static mapSort(query: SearchQuery): BackendQuerySort {
    return (query.sort && query.sort.field && query.sort.direction !== undefined) ?
    {
      field: QueryParametersMapper.getBackendSortField(query.sort.field),
      direction: Direction[query.sort.direction]
    } : undefined;
  }

  private static mapApplicationParameters(query: ApplicationSearchQuery): Array<BackendQueryParameter> {
    let queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(
      queryParameters, 'location.streetAddress', QueryParametersMapper.removeExtraWhitespace(query.address));
    QueryParametersMapper.mapParameter(queryParameters, 'applicant.name', QueryParametersMapper.removeExtraWhitespace(query.applicant));
    QueryParametersMapper.mapParameter(queryParameters, 'contacts.name', QueryParametersMapper.removeExtraWhitespace(query.contact));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'handler.userName', query.handler);
    QueryParametersMapper.mapParameter(queryParameters, 'applicationId', QueryParametersMapper.removeExtraWhitespace(query.applicationId));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'status', query.status);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'location.districtId', query.districts);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'applicationTags', query.tags);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'type', query.type);
    QueryParametersMapper.mapParameter(queryParameters, '_all', query.freeText);
    QueryParametersMapper.mapDateParameter(queryParameters, START_TIME_FIELD, MIN_DATE, query.endTime);
    QueryParametersMapper.mapDateParameter(queryParameters, END_TIME_FIELD, query.startTime, MAX_DATE);
    Some(query.projectId).do(projectId => QueryParametersMapper.mapParameter(queryParameters, 'projectId', projectId.toString()));
    return queryParameters;
  }

  private static mapProjectParameters(query: ProjectSearchQuery): Array<BackendQueryParameter> {
    let queryParameters: Array<BackendQueryParameter> = [];
    Some(query.id).do(id => QueryParametersMapper.mapParameter(queryParameters, 'id', id.toString()));
    QueryParametersMapper.mapDateParameter(queryParameters, START_TIME_FIELD, MIN_DATE, query.endTime);
    QueryParametersMapper.mapDateParameter(queryParameters, END_TIME_FIELD, query.startTime, MAX_DATE);
    QueryParametersMapper.mapParameter(queryParameters, 'ownerName', QueryParametersMapper.removeExtraWhitespace(query.ownerName));
    Some(query.onlyActive).do(onlyActive => QueryParametersMapper.mapProjectActivityParameter(queryParameters, onlyActive));
    Some(query.creator).do(creator => QueryParametersMapper.mapParameter(queryParameters, 'creator', creator.toString()));
    // TODO: Map when supported
    // QueryParametersMapper.mapParameter(queryParameters, 'district', QueryParametersMapper.removeExtraWhitespace(query.district));
    return queryParameters;
  }

  private static mapParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    parameterValue: string): void {
      if (parameterValue) {
        queryParameters.push(QueryParametersMapper.createParameter(parameterName, parameterValue));
      }
  }

  private static mapArrayParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    parameterValue: Array<string>): void {
    if (parameterValue) {
      let filteredParameterValue = parameterValue.filter(value => !!value);
      if (filteredParameterValue.length !== 0) {
        queryParameters.push(QueryParametersMapper.createArrayParameter(parameterName, filteredParameterValue));
      }
    }
  }

  private static mapDateParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    startDate: Date,
    endDate: Date): void {
    if (startDate && endDate) {
      queryParameters.push(QueryParametersMapper.createDateParameter(parameterName, startDate, endDate));
    }
  }

  private static mapProjectActivityParameter(
    queryParameters: Array<BackendQueryParameter>,
    onlyActive: boolean) {
    if (onlyActive) {
      let startTimeParameter = QueryParametersMapper.createDateParameter(START_TIME_FIELD, MIN_DATE, new Date());
      let endTimeParameter = QueryParametersMapper.createDateParameter(END_TIME_FIELD, new Date(), MAX_DATE);
      queryParameters.push(startTimeParameter);
      queryParameters.push(endTimeParameter);
    }
  }

  private static createArrayParameter(parameterName: string, parameterValue: Array<string>) {
    return {
      fieldName: QueryParametersMapper.getBackendValueField(parameterName),
      fieldValue: undefined,
      fieldMultiValue: parameterValue,
      startDateValue: undefined,
      endDateValue: undefined
    };
  }

  private static createParameter(parameterName: string, parameterValue: string) {
    return {
      fieldName: QueryParametersMapper.getBackendValueField(parameterName),
      fieldValue: parameterValue,
      fieldMultiValue: undefined,
      startDateValue: undefined,
      endDateValue: undefined
    };
  }

  private static createDateParameter(parameterName: string, startDate: Date, endDate: Date): any {
    return {
      fieldName: QueryParametersMapper.getBackendValueField(parameterName),
      fieldValue: undefined,
      fieldMultiValue: undefined,
      startDateValue: startDate.toISOString(),
      endDateValue: endDate.toISOString()
    };
  }

  private static getBackendSortField(field: string): string {
    if (enumFields.indexOf(field) > -1) {
      return field + '.ordinal';
    } else {
      return field;
    }
  }

  private static getBackendValueField(field: string): string {
    if (enumFields.indexOf(field) > -1) {
      return field + '.value';
    } else {
      return field;
    }
  }

  private static removeExtraWhitespace(str: string): string {
    let retVal = undefined;
    if (str) {
      retVal = str.trim();
      retVal = retVal.replace('\s+', ' ');
    }
    return retVal;
  }
}
