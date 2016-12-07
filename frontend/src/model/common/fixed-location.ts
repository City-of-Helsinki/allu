import {ApplicationKind} from '../application/type/application-kind';

export class FixedLocation {
  constructor()
  constructor(
    id: number,
    area: string,
    section: string,
    applicationKind: ApplicationKind,
    geometry: GeoJSON.GeometryCollection)
  constructor(
    public id?: number,
    public area?: string,
    public section?: string,
    public applicationKind?: ApplicationKind,
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
