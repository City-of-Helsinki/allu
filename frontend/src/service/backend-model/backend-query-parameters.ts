export interface BackendQueryParameters {
  queryParameters: Array<BackendQueryParameter>;
}

export interface BackendQueryParameter {
  fieldName: string;
  fieldValue: string;
  startDateValue: string;
  endDateValue: string;
}
