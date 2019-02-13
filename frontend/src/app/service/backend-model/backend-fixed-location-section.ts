import {ApplicationKind} from '@model/application/type/application-kind';

export interface BackendFixedLocationSection {
  id: number;
  name: string;
  applicationKind: ApplicationKind;
  geometry: GeoJSON.GeometryCollection;
  active: boolean;
}
