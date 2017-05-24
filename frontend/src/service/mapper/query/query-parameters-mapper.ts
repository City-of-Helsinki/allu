import {BackendQueryParameter, BackendQuerySort} from '../../backend-model/backend-query-parameters';
import {Direction} from '../../../model/common/sort';
import {Some} from '../../../util/option';
import {SearchQuery} from '../../../model/common/search-query';

export const enumFields = [
  'status',
  'type'
];

export const alphaSortFields = [
  'name',
  'customer.name',
  'contacts.name',
  'handler.userName',
  'locations.streetAddress',
  'applicationId',
  'ownerName'
];

export const START_TIME_FIELD = 'startTime';
export const END_TIME_FIELD = 'endTime';

export class QueryParametersMapper {
  public static mapSort(query: SearchQuery): BackendQuerySort {
    return (query.sort && query.sort.field && query.sort.direction !== undefined) ?
    {
      field: QueryParametersMapper.getBackendSortField(query.sort.field),
      direction: Direction[query.sort.direction]
    } : undefined;
  }

  public static mapParameter(
    queryParameters: Array<BackendQueryParameter>,
    parameterName: string,
    parameterValue: string): void {
      if (parameterValue) {
        queryParameters.push(QueryParametersMapper.createParameter(parameterName, parameterValue));
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
      let filteredParameterValue = parameterValue.filter(value => !!value);
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
    let retVal = undefined;
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

  private static createParameter(parameterName: string, parameterValue: string) {
    return {
      fieldName: QueryParametersMapper.getBackendValueField(parameterName),
      fieldValue: parameterValue,
      fieldMultiValue: undefined,
      startDateValue: undefined,
      endDateValue: undefined
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

  private static getBackendSortField(field: string): string {
    if (enumFields.indexOf(field) > -1) {
      return field + '.ordinal';
    } else if (alphaSortFields.indexOf(field) > -1) {
      return field + '.alphasort';
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
}
