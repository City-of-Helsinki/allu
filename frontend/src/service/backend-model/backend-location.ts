import {BackendPostalAddress} from './backend-postal-address';

export interface BackendLocation {
  id: number;
  geometry: GeoJSON.GeometryCollection;
  postalAddress: BackendPostalAddress;
}
