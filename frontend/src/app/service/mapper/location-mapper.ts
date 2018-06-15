import {BackendLocation} from '../backend-model/backend-location';
import {Location} from '../../model/common/location';
import {PostalAddress} from '../../model/common/postal-address';
import {TimeUtil} from '../../util/time.util';

export class LocationMapper {
  public static mapBackendList(backendLocations: Array<BackendLocation>): Array<Location> {
    return (backendLocations)
      ? backendLocations.map(loc => LocationMapper.mapBackend(loc))
      : [];
  }

  public static mapFrontendList(locations: Array<Location>): Array<BackendLocation> {
    return (locations)
      ? locations.map(loc => LocationMapper.mapFrontend(loc))
      : [];
  }

  public static mapBackend(backendLocation: BackendLocation): Location {
    return (backendLocation) ?
      new Location(
        backendLocation.id,
        backendLocation.locationKey,
        backendLocation.locationVersion,
        TimeUtil.dateFromBackend(backendLocation.startTime),
        TimeUtil.dateFromBackend(backendLocation.endTime),
        backendLocation.geometry,
        backendLocation.area,
        backendLocation.areaOverride,
        PostalAddress.fromBackend(backendLocation.postalAddress),
        backendLocation.address,
        backendLocation.fixedLocationIds,
        backendLocation.cityDistrictId,
        backendLocation.cityDistrictIdOverride,
        backendLocation.paymentTariff,
        backendLocation.paymentTariffOverride,
        backendLocation.underpass,
        backendLocation.additionalInfo) : undefined;
  }
  public static mapFrontend(location: Location): BackendLocation {
    return (location) ?
    {
      id: location.id,
      locationKey: location.locationKey,
      locationVersion: location.locationVersion,
      startTime: TimeUtil.dateToBackend(location.startTime),
      endTime: TimeUtil.dateToBackend(location.endTime),
      geometry: location.geometry,
      area: location.area,
      areaOverride: location.areaOverride,
      postalAddress: location.postalAddress.toBackend(),
      address: location.address,
      fixedLocationIds: location.fixedLocationIds,
      cityDistrictId: location.cityDistrictId,
      cityDistrictIdOverride: location.cityDistrictIdOverride,
      paymentTariff: location.paymentTariff,
      paymentTariffOverride: location.paymentTariffOverride,
      underpass: location.underpass,
      additionalInfo: location.info
    } : undefined;
  }
}
