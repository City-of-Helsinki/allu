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
