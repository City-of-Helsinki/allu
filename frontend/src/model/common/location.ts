import {PostalAddress} from './postal-address';

export class Location {
  constructor(
    public id: number,
    public geometry: GeoJSON.GeometryCollection,
    public postalAddress: PostalAddress) {};
}
