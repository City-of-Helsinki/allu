import {ApplicationKind} from '@model/application/type/application-kind';

export class FixedLocation {
  constructor(
    public id?: number,
    public area?: string,
    public section?: string,
    public applicationKind?: ApplicationKind,
    public geometry?: GeoJSON.GeometryCollection,
    public active?: boolean) {}


  get name() {
    return this.section
      ? `${this.area} - ${this.section}`
      : this.area;
  }
}

export interface FixedLocationsByAreas {
  [key: string]: FixedLocation[];
}

export function groupByArea(fixedLocations: FixedLocation[] = []): FixedLocationsByAreas {
  return fixedLocations.reduce((acc: FixedLocationsByAreas, cur: FixedLocation) => {
    if (acc[cur.area]) {
      acc[cur.area].push(cur);
    } else {
      acc[cur.area] = [cur];
    }
    return acc;
  }, {});
}

export function fixedLocationInfo(name: string, fixedLocations: FixedLocation[] = []): string {
  const sectionNames = fixedLocations.map(fl => fl.section).join(', ');
  return sectionNames
    ? `${name} - ${sectionNames}`
    : name;
}
