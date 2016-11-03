import {BackendPostalAddress} from './backend-postal-address';

export interface BackendLocation {
  id: number;
  geometry: GeoJSON.GeometryCollection;
  area: number;
  postalAddress: BackendPostalAddress;
  fixedLocationId: number;
  info: string;
}
