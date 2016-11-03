import {BackendFixedLocation} from '../backend-model/backend-fixed-location';
import {FixedLocation} from '../../model/common/fixed-location';
export class FixedLocationMapper {

  public static mapBackend(backendFixedLocation: BackendFixedLocation): FixedLocation {
    return (backendFixedLocation) ? new FixedLocation(
      backendFixedLocation.id,
      backendFixedLocation.area,
      backendFixedLocation.section)
      : undefined;
  }

  public static mapFrontend(fixedLocation: FixedLocation): BackendFixedLocation {
    return (fixedLocation) ?
    {
      id: fixedLocation.id,
      area: fixedLocation.area,
      section: fixedLocation.section
    } : undefined;
  }
}
