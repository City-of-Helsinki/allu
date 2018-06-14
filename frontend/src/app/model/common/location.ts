import {PostalAddress} from './postal-address';
import {TimeUtil} from '../../util/time.util';
import {Some} from '../../util/option';

export class Location {

  constructor(
    public id?: number,
    public locationKey?: number,
    public locationVersion?: number,
    public startTime?: Date,
    public endTime?: Date,
    public geometry?: GeoJSON.GeometryCollection,
    public area?: number,
    public areaOverride?: number,
    public postalAddress?: PostalAddress,
    public address?: string,
    public fixedLocationIds?: Array<number>,
    public cityDistrictId?: number,
    public cityDistrictIdOverride?: number,
    public paymentTariff?: number,
    public paymentTariffOverride?: number,
    public underpass?: boolean,
    public info?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
    this.fixedLocationIds = fixedLocationIds || [];
    this.underpass = underpass || false;
  }

  get uiArea(): number {
    return this.area ? Math.ceil(this.area) : undefined;
  }

  get effectiveArea(): number {
    return Number.isInteger(this.areaOverride) ? this.areaOverride : this.area;
  }

  get effectiveUiArea(): number {
    return this.effectiveArea ? Math.ceil(this.effectiveArea) : undefined;
  }

  get effectiveCityDistrictId(): number {
    return Number.isInteger(this.cityDistrictIdOverride) ? this.cityDistrictIdOverride : this.cityDistrictId;
  }

  public get uiStartTime(): string {
    return TimeUtil.getUiDateString(this.startTime);
  }

  public set uiStartTime(dateString: string) {
    this.startTime = TimeUtil.getStartDateFromUi(dateString);
  }

  public get uiEndTime(): string {
    return TimeUtil.getUiDateString(this.endTime);
  }

  public set uiEndTime(dateString: string) {
    this.endTime = TimeUtil.getEndDateFromUi(dateString);
  }

  public get uiUnderpass() {
    return this.underpass ? 'X' : '-';
  }

  public hasFixedGeometry(): boolean {
    return this.fixedLocationIds.length > 0;
  }

  public copyAsNew(): Location {
    const loc = new Location();
    loc.id = undefined;
    loc.locationKey = undefined;
    loc.locationVersion = undefined;
    loc.startTime = this.startTime;
    loc.endTime = this.endTime;
    loc.geometry = this.geometry;
    loc.area = this.area;
    loc.areaOverride = this.areaOverride;
    loc.postalAddress = this.postalAddress;
    loc.fixedLocationIds = this.fixedLocationIds;
    loc.cityDistrictId = this.cityDistrictId;
    loc.cityDistrictIdOverride = this.cityDistrictIdOverride;
    loc.paymentTariff = this.paymentTariff;
    loc.paymentTariffOverride = this.paymentTariffOverride;
    loc.underpass = this.underpass;
    loc.info = this.info;
    return loc;
  }
}
