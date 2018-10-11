export interface BackendQueryParameters {
  queryParameters: Array<BackendQueryParameter>;
  intersectingGeometry?: GeoJSON.GeometryObject;
}

export interface BackendQueryParameter {
  fieldName: string;
  fieldValue: string;
  fieldMultiValue: Array<string>;
  startDateValue: string;
  endDateValue: string;
}
