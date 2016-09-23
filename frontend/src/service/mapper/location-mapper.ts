import {BackendLocation} from '../backend-model/backend-location';
import {Location} from '../../model/common/location';
import {PostalAddress} from '../../model/common/postal-address';

export class LocationMapper {
  public static mapBackend(backendLocation: BackendLocation): Location {
    return (backendLocation) ?
      new Location(
        backendLocation.id,
        backendLocation.geometry,
        PostalAddress.fromBackend(backendLocation.postalAddress),
        backendLocation.info) : undefined;
  }
  public static mapFrontend(location: Location): BackendLocation {
    return (location) ?
    {
      id: location.id,
      geometry: location.geometry,
      postalAddress: location.postalAddress.toBackend(),
      info: location.info
    } : undefined;
  }
}
