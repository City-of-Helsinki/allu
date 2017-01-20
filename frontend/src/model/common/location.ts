import {PostalAddress} from './postal-address';

export class Location {

  constructor()
  constructor(
    id: number,
    geometry: GeoJSON.GeometryCollection,
    area: number,
    postalAddress: PostalAddress,
    fixedLocationIds: Array<number>,
    cityDistrictId: number,
    info: string)
  constructor(
    public id?: number,
    public geometry?: GeoJSON.GeometryCollection,
    public area?: number,
    public postalAddress?: PostalAddress,
    public fixedLocationIds?: Array<number>,
    public cityDistrictId?: number,
    public info?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
    this.fixedLocationIds = fixedLocationIds || [];
  };

  get uiArea(): number {
    return this.area ? Math.ceil(this.area) : undefined;
  }
}
