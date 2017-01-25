import {BackendPostalAddress} from './backend-postal-address';

export interface BackendLocation {
  id: number;
  geometry: GeoJSON.GeometryCollection;
  area: number;
  areaOverride: number;
  postalAddress: BackendPostalAddress;
  fixedLocationIds: Array<number>;
  cityDistrictId: number;
  cityDistrictIdOverride: number;
  info: string;
}
