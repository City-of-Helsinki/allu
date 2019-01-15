import {PathOptions} from 'leaflet';

export interface MapFeature {
  id: number;
  geometry: GeoJSON.GeometryCollection;
  style?: PathOptions;
}
