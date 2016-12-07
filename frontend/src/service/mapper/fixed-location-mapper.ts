import {BackendFixedLocation} from '../backend-model/backend-fixed-location';
import {FixedLocation} from '../../model/common/fixed-location';
import {ApplicationKind} from '../../model/application/type/application-kind';

export class FixedLocationMapper {

  public static mapBackend(backendFixedLocation: BackendFixedLocation): FixedLocation {
    return (backendFixedLocation) ? new FixedLocation(
      backendFixedLocation.id,
      backendFixedLocation.area,
      backendFixedLocation.section,
      ApplicationKind[backendFixedLocation.applicationKind],
      backendFixedLocation.geometry
    )
      : undefined;
  }

  public static mapFrontend(fixedLocation: FixedLocation): BackendFixedLocation {
    return (fixedLocation) ?
    {
      id: fixedLocation.id,
      area: fixedLocation.area,
      section: fixedLocation.section,
      applicationKind: ApplicationKind[fixedLocation.applicationKind],
      geometry: fixedLocation.geometry
    } : undefined;
  }
}
