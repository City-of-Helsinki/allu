export interface BackendQueryParameters {
  queryParameters: Array<BackendQueryParameter>;
}

export interface BackendQueryParameter {
  fieldName: string;
  fieldValue: string;
  fieldMultiValue: Array<string>;
  startDateValue: string;
  endDateValue: string;
}
