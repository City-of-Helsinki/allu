import {FixedLocation} from '@model/common/fixed-location';
import {ApplicationKind} from '@model/application/type/application-kind';

export interface BackendFixedLocation {
  id: number;
  area: string;
  section: string;
  applicationKind: ApplicationKind;
  geometry: GeoJSON.GeometryCollection;
  active: boolean;
}

export class FixedLocationMapper {

  public static mapBackendArray(backendFixedLocations: BackendFixedLocation[] = []): FixedLocation[] {
    return backendFixedLocations.map(fl => FixedLocationMapper.mapBackend(fl));
  }

  public static mapBackend(backendFixedLocation: BackendFixedLocation): FixedLocation {
    return new FixedLocation(
      backendFixedLocation.id,
      backendFixedLocation.area,
      backendFixedLocation.section,
      backendFixedLocation.applicationKind,
      backendFixedLocation.geometry,
      backendFixedLocation.active
    );
  }
}
