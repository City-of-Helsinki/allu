import {Injectable} from '@angular/core';
import 'leaflet';

import 'proj4leaflet';

@Injectable()
export class MapService {

  private epsg3879: L.ICRS;

  constructor() {
    this.epsg3879 = this.createCrsEPSG3879();
  }

  public getEPSG3879(): L.ICRS {
    return this.epsg3879;
  }

  private createCrsEPSG3879(): L.ICRS {
    let crsName = 'EPSG:3879';
    let bounds = [25440000, 6630000, 25571072, 6761072];
    let projDef = '+proj=tmerc +lat_0=0 +lon_0=25 +k=1 +x_0=25500000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs';
    return new L.Proj.CRS.TMS(crsName, projDef, bounds, {
      resolutions: [256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25, 0.125, 0.0625, 0.03125]
    });
  }
}
