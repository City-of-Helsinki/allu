import {BackendQueryParameters, BackendQueryParameter} from '../backend-model/backend-query-parameters';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {MAX_DATE, MIN_DATE} from '../../util/time.util';

export class QueryParametersMapper {
  public static mapFrontend(query: ApplicationSearchQuery): BackendQueryParameters {
    console.log('mapping application search query', query);
    return (query) ?
    {
      queryParameters: QueryParametersMapper.mapParameters(query)
    } : undefined;
  }

  private static mapParameters(query: ApplicationSearchQuery): Array<BackendQueryParameter> {
    let queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(
      queryParameters, 'location.streetAddress', QueryParametersMapper.removeExtraWhitespace(query.address));
    QueryParametersMapper.mapParameter(queryParameters, 'applicant.name', QueryParametersMapper.removeExtraWhitespace(query.applicant));
    QueryParametersMapper.mapParameter(queryParameters, 'contacts.name', QueryParametersMapper.removeExtraWhitespace(query.contact));
    QueryParametersMapper.mapParameter(queryParameters, 'handler', QueryParametersMapper.removeExtraWhitespace(query.handler));
    QueryParametersMapper.mapParameter(queryParameters, 'name', QueryParametersMapper.removeExtraWhitespace(query.name));
    QueryParametersMapper.mapParameter(queryParameters, 'status', QueryParametersMapper.removeExtraWhitespace(query.status));
    QueryParametersMapper.mapParameter(queryParameters, 'type', QueryParametersMapper.removeExtraWhitespace(query.type));
    QueryParametersMapper.mapParameter(queryParameters, '_all', query.freeText);
    QueryParametersMapper.mapDateParameter(queryParameters, 'startTime', MIN_DATE, query.startTime);
    QueryParametersMapper.mapDateParameter(queryParameters, 'endTime', query.endTime, MAX_DATE);
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

  private static mapDateParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    startDate: Date,
    endDate: Date): void {
    if (startDate && endDate) {
      queryParameters.push(QueryParametersMapper.createDateParameter(parameterName, startDate, endDate));
    }
  }

  private static createParameter(parameterName: string, parameterValue: string) {
    return {fieldName: parameterName, fieldValue: parameterValue, startDateValue: undefined, endDateValue: undefined};
  }

  private static createDateParameter(parameterName: string, startDate: Date, endDate: Date): any {
    return {fieldName: parameterName, fieldValue: undefined, startDateValue: startDate.toISOString(), endDateValue: endDate.toISOString()};
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
