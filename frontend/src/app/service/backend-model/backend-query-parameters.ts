export interface BackendQueryParameters {
  queryParameters: Array<BackendQueryParameter>;
  intersectingGeometry?: GeoJSON.GeometryObject;
  surveyRequired?: boolean;
  zoom?: number;
}

export interface BackendQueryParameter {
  fieldName: string;
  fieldValue: string;
  fieldMultiValue: Array<string>;
  startDateValue: string;
  endDateValue: string;
}
