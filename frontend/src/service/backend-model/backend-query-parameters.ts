export interface BackendQueryParameters {
  queryParameters: Array<BackendQueryParameter>;
  sort: BackendQuerySort;
}

export interface BackendQueryParameter {
  fieldName: string;
  fieldValue: string;
  fieldMultiValue: Array<string>;
  startDateValue: string;
  endDateValue: string;
}

export interface BackendQuerySort {
  field: string;
  direction: string;
}
