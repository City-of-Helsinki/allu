import {BackendPostalAddress} from './backend-postal-address';

export interface BackendSupervisionTaskLocation {
  id: number;
  applicationLocationId: number;
  locationKey: number;
  startTime: string;
  endTime: string;
  geometry: GeoJSON.GeometryCollection;
  paymentTariff: string;
  underpass: boolean;
}

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
  locationKey: number;
  address: string;
  cityDistrictId: number;
  geometry: string;
}
