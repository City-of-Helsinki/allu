import {BackendPostalAddress} from './backend-postal-address';

export interface BackendLocation {
  id: number;
  geometry: GeoJSON.GeometryCollection;
  area: number;
  postalAddress: BackendPostalAddress;
  fixedLocationIds: Array<number>;
  cityDistrictId: number;
  info: string;
}
