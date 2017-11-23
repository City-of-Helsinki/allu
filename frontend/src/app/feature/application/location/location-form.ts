import {Location} from '../../../model/common/location';
import {Some} from '../../../util/option';
export class LocationForm {
  constructor(
    public id?: number,
    public locationKey?: number,
    public locationVersion?: number,
    public startTime?: string,
    public endTime?: string,
    public geometry?: GeoJSON.GeometryCollection,
    public sections?: Array<number>,
    public areaSize?: number,
    public areaOverride?: number,
    public streetAddress?: string,
    public postalCode?: string,
    public city?: string,
    public cityDistrictId?: number,
    public cityDistrictIdOverride?: number,
    public underpass?: boolean,
    public info?: string) {}

  static from(location: Location): LocationForm {
    const form = new LocationForm();
    form.id = location.id;
    form.locationKey = location.locationKey;
    form.locationVersion = location.locationVersion;
    form.startTime = location.uiStartTime;
    form.endTime = location.uiEndTime;
    form.geometry = location.geometry;
    form.sections = location.fixedLocationIds;
    form.areaSize = location.uiArea;
    form.areaOverride = location.areaOverride;
    Some(location.postalAddress).do(address => {
      form.streetAddress = address.streetAddress;
      form.postalCode = address.postalCode;
      form.city = address.city;
    });
    form.cityDistrictId = location.cityDistrictId;
    form.cityDistrictIdOverride = location.cityDistrictIdOverride;
    form.underpass = location.underpass;
    form.info = location.info;
    return form;
  }

  static to(form: LocationForm): Location {
    const location = new Location();
    location.id = form.id;
    location.locationKey = form.locationKey;
    location.locationVersion = form.locationVersion;
    location.uiStartTime = form.startTime;
    location.uiEndTime = form.endTime;
    location.geometry = form.geometry;
    location.fixedLocationIds = form.sections;
    location.area = form.areaSize;
    location.areaOverride = form.areaOverride;
    if (form.streetAddress) {
      location.postalAddress.streetAddress = form.streetAddress;
      location.postalAddress.postalCode = form.postalCode;
      location.postalAddress.city = form.city;
    }
    location.cityDistrictId = form.cityDistrictId;
    location.cityDistrictIdOverride = form.cityDistrictIdOverride;
    location.underpass = form.underpass;
    location.info = form.info;
    return location;
  }
}
