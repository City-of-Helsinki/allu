/*
 * Extend Leaflet typings with some support for projections i.e. proj4leaflet library.
 */

declare namespace L {
  namespace Proj {
    namespace TMS {
      export interface TMSStatic extends L.ClassStatic {
        new(code: string, def: string, projectedBounds: Array<number>, options: any): L.ICRS;
      }
    }

    export interface CRSStatic extends L.ClassStatic {
      TMS: TMS.TMSStatic;
    }
  }

  export interface ProjStatic extends L.ClassStatic {
    CRS: Proj.CRSStatic;
  }

  /* tslint:disable */ export var Proj: ProjStatic;
}
