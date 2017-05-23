/*
 * Extend Leaflet typings with some support for projections i.e. proj4leaflet library.
 */

declare namespace L {
  namespace Proj {
    export interface Projection extends L.Projection {}

    export interface CRS extends L.CRS {
      projection: L.Proj.Projection;
      new(code: string, def: string, options: any): CRS;
    }
  }

  export interface Proj extends Class {
    CRS: Proj.CRS;
  }

  /* tslint:disable */ export var Proj: Proj;
}
