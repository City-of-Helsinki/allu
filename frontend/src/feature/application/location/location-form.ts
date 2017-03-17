import {Location} from '../../../model/common/location';
import {Some} from '../../../util/option';
export class LocationForm {
  constructor(
    public startTime?: string,
    public endTime?: string,
    public streetAddress?: string,
    public areaSize?: number,
    public sections?: Array<number>,
    public info?: string,
    public areaOverride?: number,
    public cityDistrictIdOverride?: number) {}

  static from(location: Location): LocationForm {
    return new LocationForm(
      location.uiStartTime,
      location.uiEndTime,
      Some(location.postalAddress).map(address => address.streetAddress).orElse(undefined),
      location.uiArea,
      location.fixedLocationIds,
      location.info,
      location.areaOverride,
      location.cityDistrictIdOverride
    );
  }
}
