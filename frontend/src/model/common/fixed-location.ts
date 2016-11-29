import {ApplicationType} from '../application/type/application-type';
export class FixedLocation {
  constructor()
  constructor(
    id: number,
    area: string,
    section: string,
    applicationType: ApplicationType,
    geometry: GeoJSON.GeometryCollection)
  constructor(
    public id?: number,
    public area?: string,
    public section?: string,
    public applicationType?: ApplicationType,
    public geometry?: GeoJSON.GeometryCollection) { }

  static sortBySection = (left, right) => {
    if (left.section > right.section) {
      return 1;
    }
    if (left.section < right.section) {
      return -1;
    }
    // a must be equal to b
    return 0;
  };
}
