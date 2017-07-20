package fi.hel.allu.external.mapper;

import fi.hel.allu.external.domain.LocationExt;
import fi.hel.allu.servicecore.domain.LocationJson;

/**
 * Mapping between <code>LocationJson</code> and <code>LocationExt</code> classes.
 */
public class LocationExtMapper {

  /**
   * Creates new location json from given external location.
   *
   * @param locationExt Location used to generate new location.
   * @return  New location.
   */
  public static LocationJson createLocationJson(LocationExt locationExt) {
    LocationJson locationJson = new LocationJson();
    locationJson.setStartTime(locationExt.getStartTime());
    locationJson.setEndTime(locationExt.getEndTime());
    locationJson.setGeometry(locationExt.getGeometry());
    // TODO: create mapping for postal address
//    locationJson.setPostalAddress(locationExt.getPostalAddress());
    locationJson.setUnderpass(locationExt.getUnderpass());
    return locationJson;
  }

  public static LocationExt mapLocationExt(LocationJson locationJson) {
    LocationExt locationExt = new LocationExt();

    locationExt.setId(locationJson.getId());
    locationExt.setLocationKey(locationJson.getLocationKey());
    locationExt.setLocationVersion(locationJson.getLocationVersion());
    locationExt.setStartTime(locationJson.getStartTime());
    locationExt.setEndTime(locationJson.getEndTime());
    locationExt.setGeometry(locationJson.getGeometry());
    locationExt.setArea(locationJson.getArea());
    locationExt.setAreaOverride(locationJson.getAreaOverride());
    // TODO: create mapping for postal address
//    locationExt.setPostalAddress(locationJson.getPostalAddress());
    locationExt.setPaymentTariff(locationJson.getPaymentTariff());
    locationExt.setPaymentTariffOverride(locationJson.getPaymentTariffOverride());
    locationExt.setUnderpass(locationJson.getUnderpass());

    return locationExt;
  }
}
