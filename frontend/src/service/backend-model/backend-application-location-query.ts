export interface BackendApplicationLocationQuery {
  after: string;
  before: string;
  statusTypes: Array<string>;
  intersectingGeometry: GeoJSON.GeometryObject;
}
