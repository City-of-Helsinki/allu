import {ApplicationKind} from '../application/type/application-kind';

export class FixedLocationSection {
  constructor(
    public id?: number,
    public name?: string,
    public applicationKind?: ApplicationKind,
    public geometry?: GeoJSON.GeometryCollection,
    public active?: boolean) { }
}
