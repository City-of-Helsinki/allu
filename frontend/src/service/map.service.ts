import {Injectable} from '@angular/core';
import 'leaflet';

import 'proj4leaflet';

@Injectable()
export class MapService {

  private epsg3879: L.ICRS;

  public featureCollectionToGeometryCollection(
    featureCollection: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>): GeoJSON.GeometryCollection {
    let geometryCollection: GeoJSON.GeometryCollection = undefined;
    if (featureCollection && featureCollection.features) {
      let features = featureCollection.features;
      let geometries: GeoJSON.GeometryObject[] = features.map(f => f.geometry);
      geometryCollection = {
        type: 'GeometryCollection',
        crs: {
          properties: {name: 'EPSG:3879'},
          type: 'name'
        },
        geometries: geometries.map(g => this.mapWgs84Geometry(g))
      };
    }
    return geometryCollection;
  }

  public geometryCollectionToFeatureCollection(
    geometryCollection: GeoJSON.GeometryCollection): GeoJSON.FeatureCollection<GeoJSON.GeometryObject> {
    if (geometryCollection && geometryCollection.geometries) {
      let geometries: GeoJSON.GeometryObject[] = geometryCollection.geometries;
      let features: GeoJSON.Feature<GeoJSON.GeometryObject>[] = geometries.map(g => this.createFeature(g, undefined));
      return {
        type: 'FeatureCollection',
        features: features
      };
    } else {
      return undefined;
    }
  }

  constructor() {
    this.epsg3879 = this.createCrsEPSG3879();
  }

  public getEPSG3879(): L.ICRS {
    return this.epsg3879;
  }

  public wgs84ToEpsg3879(coordinate: Array<number>): Array<number> {
    let myProj = this.getEPSG3879();
    let projected = myProj.projection.project(new L.LatLng(coordinate[1], coordinate[0]));
    return [projected.x, projected.y];
  }

  public epsg3879ToWgs84(coordinate: Array<number>): Array<number> {
    let myProj = this.getEPSG3879();
    let projected = myProj.projection.unproject(new L.Point(coordinate[0], coordinate[1]));
    return [projected.lng, projected.lat];
  }

  private createCrsEPSG3879(): L.ICRS {
    let crsName = 'EPSG:3879';
    let bounds = [25440000, 6630000, 25571072, 6761072];
    let projDef = '+proj=tmerc +lat_0=0 +lon_0=25 +k=1 +x_0=25500000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs';
    return new L.Proj.CRS.TMS(crsName, projDef, bounds, {
      resolutions: [256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25, 0.125, 0.0625, 0.03125]
    });
  }

  private createFeature(geometry: GeoJSON.GeometryObject, properties: any): GeoJSON.Feature<GeoJSON.GeometryObject> {
    return {
      type: 'Feature',
      geometry: this.mapEPSG3879Geometry(geometry),
      properties: undefined
    };
  }

  private mapWgs84Geometry(geometry: any): any {
    return { type: geometry.type, coordinates: this.mapWgs84GeometryArray(geometry.coordinates) };
  }

  private mapWgs84GeometryArray(geometryArray: Array<Array<Array<number>>>): Array<Array<Array<number>>> {
    return geometryArray.map(ga => this.mapWgs84CoordinateArray(ga));
  }

  private mapWgs84CoordinateArray(coordinateArray: Array<Array<number>>): Array<Array<number>> {
    return coordinateArray.map(c => this.wgs84ToEpsg3879(c));
  }

  private mapEPSG3879Geometry(geometry: any): any {
    return { type: geometry.type, coordinates: this.mapEPSG3879GeometryArray(geometry.coordinates) };
  }

  private mapEPSG3879GeometryArray(geometryArray: Array<Array<Array<number>>>): Array<Array<Array<number>>> {
    return geometryArray.map(ga => this.mapEPSG3879CoordinateArray(ga));
  }

  private mapEPSG3879CoordinateArray(coordinateArray: Array<Array<number>>): Array<Array<number>> {
    return coordinateArray.map(c => this.epsg3879ToWgs84(c));
  }
}
