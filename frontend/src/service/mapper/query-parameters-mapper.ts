import {BackendQueryParameters, BackendQueryParameter, BackendQuerySort} from '../backend-model/backend-query-parameters';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {MAX_DATE, MIN_DATE} from '../../util/time.util';
import {Direction} from '../../model/common/sort';

const enumFields = [
  'status',
  'type'
];

export class QueryParametersMapper {
  public static mapFrontend(query: ApplicationSearchQuery): BackendQueryParameters {
    return (query) ?
    {
      queryParameters: QueryParametersMapper.mapParameters(query),
      sort: QueryParametersMapper.mapSort(query)
    } : undefined;
  }

  private static mapSort(query: ApplicationSearchQuery): BackendQuerySort {
    return (query.sort && query.sort.field && query.sort.direction !== undefined) ?
    {
      field: QueryParametersMapper.getBackendSortField(query.sort.field),
      direction: Direction[query.sort.direction]
    } : undefined;
  }

  private static mapParameters(query: ApplicationSearchQuery): Array<BackendQueryParameter> {
    let queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(
      queryParameters, 'location.streetAddress', QueryParametersMapper.removeExtraWhitespace(query.address));
    QueryParametersMapper.mapParameter(queryParameters, 'applicant.name', QueryParametersMapper.removeExtraWhitespace(query.applicant));
    QueryParametersMapper.mapParameter(queryParameters, 'contacts.name', QueryParametersMapper.removeExtraWhitespace(query.contact));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'handler.userName', query.handler);
    QueryParametersMapper.mapParameter(queryParameters, 'applicationId', QueryParametersMapper.removeExtraWhitespace(query.applicationId));
    QueryParametersMapper.mapArrayParameter(queryParameters, 'status', query.status);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'type', query.type);
    QueryParametersMapper.mapParameter(queryParameters, '_all', query.freeText);
    QueryParametersMapper.mapDateParameter(queryParameters, 'startTime', MIN_DATE, query.endTime);
    QueryParametersMapper.mapDateParameter(queryParameters, 'endTime', query.startTime, MAX_DATE);
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
