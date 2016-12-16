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


  export var extend;
  // localization for draw toolbars
  export var drawLocal: {
    draw: {
      toolbar: {
        actions: {
          title: string,
          text: string
        },
        finish: {
          title: string,
          text: string
        },
        undo: {
          title: string,
          text: string
        },
        buttons: {
          polyline: string,
          polygon: string,
          rectangle: string,
          circle: string,
          marker: string
        }
      },
      handlers: {
        circle: {
          tooltip: {
            start: string
          },
          radius: string
        },
        marker: {
          tooltip: {
            start: string
          }
        },
        polygon: {
          tooltip: {
            start: string,
            cont: string,
            end: string
          }
        },
        polyline: {
          error: string,
          tooltip: {
            start: string,
            cont: string,
            end: string
          }
        },
        rectangle: {
          tooltip: {
            start: string
          }
        },
        simpleshape: {
          tooltip: {
            end: string
          }
        }
      }
    },
    edit: {
      toolbar: {
        actions: {
          save: {
            title: string,
            text: string
          },
          cancel: {
            title: string,
            text: string
          }
        },
        buttons: {
          edit: string,
          editDisabled: string,
          remove: string,
          removeDisabled: string
        }
      },
      handlers: {
        edit: {
          tooltip: {
            text: string,
            subtext: string
          }
        },
        remove: {
          tooltip: {
            text: string
          }
        }
      }
    }
  };
}
