import {BackendQueryParameter} from '../../backend-model/backend-query-parameters';
import {Sort} from '../../../model/common/sort';
import {Some} from '../../../util/option';
import {PageRequest} from '../../../model/common/page-request';
import {HttpParams} from '@angular/common/http';

export const enumFields = [
  'status',
  'type'
];

export const START_TIME_FIELD = 'startTime';
export const END_TIME_FIELD = 'endTime';

export class QueryParametersMapper {
  public static mapSortToSearchServiceQuery(sort: Sort): HttpParams {
    if (sort) {
      return QueryParametersMapper.mapSortToQueryParameters(
        new Sort(sort.field, sort.direction)
      );
    }
    return new HttpParams();
  }
  public static mapSortToQueryParameters(sort: Sort): HttpParams {
    const sortParams = QueryParametersMapper.sortToSortParams(sort);
    let params = new HttpParams();
    params = Some(sortParams).map(sp => params.append('sort', sp)).orElse(new HttpParams());
    return params;
  }

  public static mapPageRequest(pageRequest: PageRequest, sort: Sort, matchAny?: boolean): HttpParams {
    let params = new HttpParams();
    params = Some(pageRequest).map(pr => pr.page).map(page => params.append('page', page.toString())).orElse(params);
    params = Some(pageRequest).map(pr => pr.size).map(size => params.append('size', size.toString())).orElse(params);
    params = Some(sort).map(s => QueryParametersMapper.sortToSortParams(s)).map(sp => params.append('sort', sp)).orElse(params);
    params = Some(matchAny).map(m => params.append('matchAny', String(m))).orElse(params);
    return params;
  }

  public static mapParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    parameterValue: string,
    boost?: number): void {
      if (parameterValue) {
        queryParameters.push(QueryParametersMapper.createParameter(parameterName, parameterValue, boost));
      }
  }

  public static mapRawParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    parameterValue: string): void {
    if (parameterValue) {
      queryParameters.push(QueryParametersMapper.createRawParameter(parameterName, parameterValue));
    }
  }

  public static mapArrayParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    parameterValue: Array<string>): void {
    if (parameterValue) {
      const filteredParameterValue = parameterValue.filter(value => !!value);
      if (filteredParameterValue.length !== 0) {
        queryParameters.push(QueryParametersMapper.createArrayParameter(parameterName, filteredParameterValue));
      }
    }
  }

  public static mapDateParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    startDate: Date,
    endDate: Date,
    open: boolean = false): void {
    Some(QueryParametersMapper.createDateParameter(parameterName, startDate, endDate, open))
      .do(param => queryParameters.push(param));
  }

  public static mapBooleanParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    parameterValue: boolean): void {
    if (parameterValue !== undefined) {
      queryParameters.push(QueryParametersMapper.createBooleanParameter(parameterName, parameterValue));
    }
  }

  public static removeExtraWhitespace(str: string): string {
    let retVal;
    if (str) {
      retVal = str.trim();
      retVal = retVal.replace('\s+', ' ');
    }
    return retVal;
  }

  public static createDateParameter(parameterName: string, startDate: Date, endDate: Date, open: boolean = false): any {
    /**
     * Open allows other date parameter to be undefined, otherwise require both to be defined
     */
    if (open || (startDate && endDate)) {
      return {
        fieldName: QueryParametersMapper.getBackendValueField(parameterName),
        fieldValue: undefined,
        fieldMultiValue: undefined,
        startDateValue: !!startDate ? startDate.toISOString() : undefined,
        endDateValue: !!endDate ? endDate.toISOString() : undefined
      };
    } else {
      return undefined;
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

  private static createRawParameter(parameterName: string, parameterValue: string) {
    return {
      fieldName: parameterName,
      fieldValue: parameterValue,
      fieldMultiValue: undefined,
      startDateValue: undefined,
      endDateValue: undefined
    };
  }

  private static createParameter(parameterName: string, parameterValue: string, boost?: number) {
    return {
      fieldName: QueryParametersMapper.getBackendValueField(parameterName),
      fieldValue: parameterValue,
      fieldMultiValue: undefined,
      startDateValue: undefined,
      endDateValue: undefined,
      boost: boost
    };
  }

  private static createBooleanParameter(parameterName: string, parameterValue: boolean) {
      return {
        fieldName: QueryParametersMapper.getBackendValueField(parameterName),
        fieldValue: Some(parameterValue).map(val => val.toString()).orElse(undefined),
        fieldMultiValue: undefined,
        startDateValue: undefined,
        endDateValue: undefined
      };
  }

  private static getBackendValueField(field: string): string {
    if (enumFields.indexOf(field) > -1) {
      return field + '.value';
    } else {
      return field;
    }
  }

  private static sortToSortParams(sort: Sort): string {
    let sortParam = [];
    if (sort) {
      sortParam = [sort.field];
      sortParam = Some(sort.direction).map(dir => sortParam.concat(dir)).orElse(sortParam);
    }
    return sortParam.join(',');
  }
}
