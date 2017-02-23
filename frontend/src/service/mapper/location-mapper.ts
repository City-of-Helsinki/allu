import {BackendLocation} from '../backend-model/backend-location';
import {Location} from '../../model/common/location';
import {PostalAddress} from '../../model/common/postal-address';

export class LocationMapper {
  public static mapBackend(backendLocation: BackendLocation): Location {
    return (backendLocation) ?
      new Location(
        backendLocation.id,
        backendLocation.locationKey,
        backendLocation.locationVersion,
        backendLocation.geometry,
        backendLocation.area,
        backendLocation.areaOverride,
        PostalAddress.fromBackend(backendLocation.postalAddress),
        backendLocation.fixedLocationIds,
        backendLocation.cityDistrictId,
        backendLocation.cityDistrictIdOverride,
        backendLocation.paymentTariff,
        backendLocation.paymentTariffOverride,
        backendLocation.underpass,
        backendLocation.info) : undefined;
  }
  public static mapFrontend(location: Location): BackendLocation {
    return (location) ?
    {
      id: location.id,
      locationKey: location.locationKey,
      locationVersion: location.locationVersion,
      geometry: location.geometry,
      area: location.area,
      areaOverride: location.areaOverride,
      postalAddress: location.postalAddress.toBackend(),
      fixedLocationIds: location.fixedLocationIds,
      cityDistrictId: location.cityDistrictId,
      cityDistrictIdOverride: location.cityDistrictIdOverride,
      paymentTariff: location.paymentTariff,
      paymentTariffOverride: location.paymentTariffOverride,
      underpass: location.underpass,
      info: location.info
    } : undefined;
  }
}
