import {PostalAddress} from './postal-address';

export class Location {

  constructor()
  constructor(
    id: number,
    geometry: GeoJSON.GeometryCollection,
    area: number,
    postalAddress: PostalAddress,
    fixedLocationId: number,
    info: string)
  constructor(
    public id?: number,
    public geometry?: GeoJSON.GeometryCollection,
    public area?: number,
    public postalAddress?: PostalAddress,
    public fixedLocationId?: number,
    public info?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
  };

  get uiArea(): number {
    return this.area ? Math.ceil(this.area) : undefined;
  }
}
