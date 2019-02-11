export interface BackendFixedLocationSection {
  id: number;
  name: string;
  applicationKind: string;
  geometry: GeoJSON.GeometryCollection;
  active: boolean;
}
