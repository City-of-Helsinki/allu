import {PostalAddress} from './postal-address';

export class Location {

  constructor()
  constructor(
    id: number,
    locationKey: number,
    locationVersion: number,
    startTime: Date,
    endTime: Date,
    geometry: GeoJSON.GeometryCollection,
    area: number,
    areaOverride: number,
    postalAddress: PostalAddress,
    fixedLocationIds: Array<number>,
    cityDistrictId: number,
    cityDistrictIdOverride: number,
    paymentTariff: number,
    paymentTariffOverride: number,
    underpass: boolean,
    info: string)
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
  };

  get uiArea(): number {
    return this.area ? Math.ceil(this.area) : undefined;
  }

  get effectiveCityDistrictId(): number {
    return Number.isInteger(this.cityDistrictIdOverride) ? this.cityDistrictIdOverride : this.cityDistrictId;
  }
}
