import {Injectable} from '@angular/core';
import * as L from 'leaflet';
import 'proj4leaflet';
import {Position} from 'geojson';

function isPosition(coordinates: any[]): coordinates is Position {
  return coordinates === undefined || coordinates.length === 0 || !Array.isArray(coordinates[0]);
}

@Injectable()
export class Projection {
  readonly EPSG3879: L.Proj.CRS;

  constructor() {
    const crsName = 'EPSG:3879';
    const bounds = L.bounds([25440000, 6630000], [25571072, 6761072]);
    const projDef = '+proj=tmerc +lat_0=0 +lon_0=25 +k=1 +x_0=25500000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs';
    this.EPSG3879 = new L.Proj.CRS(crsName, projDef, {
      resolutions: [128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25, 0.125, 0.0625, 0.03125],
      bounds: bounds,
      origin: [25440000, 6630000]
    });
  }

  public project(coordinates: any[]): any[] {
    if (isPosition(coordinates)) {
      return this.projectPosition(coordinates);
    } else {
      return coordinates.map(c => this.project(c));
    }
  }

  public unproject(coordinates: any[]): any[] {
    if (isPosition(coordinates)) {
      return this.unprojectPosition(coordinates);
    } else {
      return coordinates.map(c => this.unproject(c));
    }
  }

  private projectPosition(position: Position): Position {
    const projected = this.EPSG3879.projection.project(L.latLng(position[1], position[0]));
    return [projected.x, projected.y];
  }

  private unprojectPosition(position: Position): Position {
    const projected = this.EPSG3879.projection.unproject(L.point(position[0], position[1]));
    return [projected.lng, projected.lat];
  }
}
