import {PostalAddress} from './postal-address';

export class Location {

  constructor()
  constructor(
    id: number,
    geometry: GeoJSON.GeometryCollection,
    postalAddress: PostalAddress,
    info: string)
  constructor(
    public id?: number,
    public geometry?: GeoJSON.GeometryCollection,
    public postalAddress?: PostalAddress,
    public info?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
  };
}
