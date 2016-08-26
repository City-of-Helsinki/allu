import {BackendQueryParameters, BackendQueryParameter} from '../backend-model/backend-query-parameters';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';

export class QueryParametersMapper {
  public static mapFrontend(query: ApplicationSearchQuery): BackendQueryParameters {
    console.log('mapping appication search query');
    return (query) ?
    {
      queryParameters: QueryParametersMapper.mapParameters(query)
    } : undefined;
  }

  private static mapParameters(query: ApplicationSearchQuery): Array<BackendQueryParameter> {
    let queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapParameter(queryParameters, 'streetAddress', QueryParametersMapper.removeExtraWhitespace(query.address));
    // TODO: check what's the correct query field for applicant. Applicant was not indexed at the time of writing this comment
    QueryParametersMapper.mapParameter(queryParameters, 'applicant', QueryParametersMapper.removeExtraWhitespace(query.applicant));
    // TODO: check what's the correct query field for contact. Contact was not indexed at the time of writing this comment
    QueryParametersMapper.mapParameter(queryParameters, 'contact', QueryParametersMapper.removeExtraWhitespace(query.contact));
    QueryParametersMapper.mapParameter(queryParameters, 'handler', QueryParametersMapper.removeExtraWhitespace(query.handler));
    QueryParametersMapper.mapParameter(queryParameters, 'name', QueryParametersMapper.removeExtraWhitespace(query.name));
    QueryParametersMapper.mapParameter(queryParameters, 'status', QueryParametersMapper.removeExtraWhitespace(query.status));
    QueryParametersMapper.mapParameter(queryParameters, 'type', QueryParametersMapper.removeExtraWhitespace(query.type));
    QueryParametersMapper.mapParameter(queryParameters, '_all', query.freeText);
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

  private static createParameter(parameterName: string, parameterValue: string) {
    return {fieldName: parameterName, fieldValue: parameterValue, startDateValue: undefined, endDateValue: undefined};
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
