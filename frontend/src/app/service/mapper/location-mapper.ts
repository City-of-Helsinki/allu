import {BackendLocation, SearchResultLocation, BackendSupervisionTaskLocation} from '../backend-model/backend-location';
import {Location} from '../../model/common/location';
import {PostalAddress} from '../../model/common/postal-address';
import {TimeUtil} from '../../util/time.util';

export class LocationMapper {
  public static mapBackendList(backendLocations: Array<BackendLocation>): Array<Location> {
    return (backendLocations)
      ? backendLocations.map(loc => LocationMapper.mapBackend(loc))
      : [];
  }

  public static mapSearchResultList(backendLocations: Array<SearchResultLocation>): Array<Location> {
    return (backendLocations)
      ? backendLocations.map(loc => LocationMapper.mapSearchResult(loc))
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
        backendLocation.additionalInfo,
        TimeUtil.dateFromBackend(backendLocation.customerStartTime),
        TimeUtil.dateFromBackend(backendLocation.customerEndTime),
        TimeUtil.dateFromBackend(backendLocation.customerReportingTime)) : undefined;
  }

  public static mapSearchResult(backendLocation: SearchResultLocation): Location {
    if (backendLocation) {
      const location = new Location();
      location.locationKey = backendLocation.locationKey;
      location.address = backendLocation.address;
      location.cityDistrictId = backendLocation.cityDistrictId;
      location.geometry = JSON.parse(backendLocation.geometry);
      return location;
    } else {
      return undefined;
    }
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

  public static mapBackendSupervisionTaskLocations(supervisionTaskLocations: BackendSupervisionTaskLocation[]): Location[] {
    return (supervisionTaskLocations)
    ? supervisionTaskLocations.map(loc => LocationMapper.mapBackendSupervisionTaskLocation(loc))
    : [];
  }

  public static mapBackendSupervisionTaskLocation(supervisionTaskLocation: BackendSupervisionTaskLocation): Location {
    const location = new Location();
    location.id = supervisionTaskLocation.applicationLocationId;
    location.startTime = TimeUtil.dateFromBackend(supervisionTaskLocation.startTime);
    location.endTime = TimeUtil.dateFromBackend(supervisionTaskLocation.endTime);
    location.geometry = supervisionTaskLocation.geometry;
    location.paymentTariff = supervisionTaskLocation.paymentTariff;
    location.locationKey = supervisionTaskLocation.locationKey;
    location.underpass = supervisionTaskLocation.underpass;
    location.customerStartTime = TimeUtil.dateFromBackend(supervisionTaskLocation.customerStartTime);
    location.customerEndTime = TimeUtil.dateFromBackend(supervisionTaskLocation.customerEndTime);
    location.customerReportingTime = TimeUtil.dateFromBackend(supervisionTaskLocation.customerReportingTime);
    return location;
  }
}
