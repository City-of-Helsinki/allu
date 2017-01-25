import {BackendLocation} from '../backend-model/backend-location';
import {Location} from '../../model/common/location';
import {PostalAddress} from '../../model/common/postal-address';

export class LocationMapper {
  public static mapBackend(backendLocation: BackendLocation): Location {
    return (backendLocation) ?
      new Location(
        backendLocation.id,
        backendLocation.geometry,
        backendLocation.area,
        backendLocation.areaOverride,
        PostalAddress.fromBackend(backendLocation.postalAddress),
        backendLocation.fixedLocationIds,
        backendLocation.cityDistrictId,
        backendLocation.cityDistrictIdOverride,
        backendLocation.info) : undefined;
  }
  public static mapFrontend(location: Location): BackendLocation {
    return (location) ?
    {
      id: location.id,
      geometry: location.geometry,
      area: location.area,
      areaOverride: location.areaOverride,
      postalAddress: location.postalAddress.toBackend(),
      fixedLocationIds: location.fixedLocationIds,
      cityDistrictId: location.cityDistrictId,
      cityDistrictIdOverride: location.cityDistrictIdOverride,
      info: location.info
    } : undefined;
  }
}
