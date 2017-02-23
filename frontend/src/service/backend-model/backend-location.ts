import {BackendPostalAddress} from './backend-postal-address';

export interface BackendLocation {
  id: number;
  locationKey: number;
  locationVersion: number;
  geometry: GeoJSON.GeometryCollection;
  area: number;
  areaOverride: number;
  postalAddress: BackendPostalAddress;
  fixedLocationIds: Array<number>;
  cityDistrictId: number;
  cityDistrictIdOverride: number;
  paymentTariff: number;
  paymentTariffOverride: number;
  underpass: boolean;
  info: string;
}
