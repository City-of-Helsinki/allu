import {BackendLocation} from '../backend-model/backend-location';
import {Location} from '../../model/common/location';

export class LocationMapper {
  public static mapBackend(backendLocation: BackendLocation): Location {
    return (backendLocation) ?
      new Location(
        backendLocation.id,
        backendLocation.geometry,
        backendLocation.postalAddress,
        backendLocation.info) : undefined;
  }
  public static mapFrontend(location: Location): BackendLocation {
    return (location) ?
    {
      id: location.id,
      geometry: location.geometry,
      postalAddress: location.postalAddress,
      info: location.info
    } : undefined;
  }
}
