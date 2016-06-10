import {PostalAddress} from './postal-address';

export class Location {
  constructor(
    public id: number,
    public geometry: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>,
    public postalAddress: PostalAddress) {};
}
