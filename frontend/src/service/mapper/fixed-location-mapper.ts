import {BackendFixedLocation} from '../backend-model/backend-fixed-location';
import {FixedLocation} from '../../model/common/fixed-location';
import {ApplicationType} from '../../model/application/type/application-type';

export class FixedLocationMapper {

  public static mapBackend(backendFixedLocation: BackendFixedLocation): FixedLocation {
    return (backendFixedLocation) ? new FixedLocation(
      backendFixedLocation.id,
      backendFixedLocation.area,
      backendFixedLocation.section,
      ApplicationType[backendFixedLocation.applicationType],
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
      applicationType: ApplicationType[fixedLocation.applicationType],
      geometry: fixedLocation.geometry
    } : undefined;
  }
}
