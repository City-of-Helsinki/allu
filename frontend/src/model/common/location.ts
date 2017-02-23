import {PostalAddress} from './postal-address';

export class Location {

  constructor()
  constructor(
    id: number,
    geometry: GeoJSON.GeometryCollection,
    area: number,
    areaOverride: number,
    postalAddress: PostalAddress,
    fixedLocationIds: Array<number>,
    cityDistrictId: number,
    cityDistrictIdOverride: number,
    info: string)
  constructor(
    public id?: number,
    public geometry?: GeoJSON.GeometryCollection,
    public area?: number,
    public areaOverride?: number,
    public postalAddress?: PostalAddress,
    public fixedLocationIds?: Array<number>,
    public cityDistrictId?: number,
    public cityDistrictIdOverride?: number,
    public info?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
    this.fixedLocationIds = fixedLocationIds || [];
  };

  get uiArea(): number {
    return this.area ? Math.ceil(this.area) : undefined;
  }

  get effectiveCityDistrictId(): number {
    return Number.isInteger(this.cityDistrictIdOverride) ? this.cityDistrictIdOverride : this.cityDistrictId;
  }
}
