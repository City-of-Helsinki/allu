import {BackendPostalAddress} from './backend-postal-address';

export interface BackendLocation {
  id: number;
  locationKey: number;
  locationVersion: number;
  startTime: string;
  endTime: string;
  geometry: GeoJSON.GeometryCollection;
  area: number;
  areaOverride: number;
  postalAddress: BackendPostalAddress;
  address: string;
  fixedLocationIds: Array<number>;
  cityDistrictId: number;
  cityDistrictIdOverride: number;
  paymentTariff: string;
  paymentTariffOverride: string;
  underpass: boolean;
  additionalInfo: string;
  customerStartTime?: string;
  customerEndTime?: string;
  customerReportingTime?: string;
}

export interface SearchResultLocation {
  address: string;
  cityDistrictId: number;
  geometry: GeoJSON.GeometryCollection;
}
