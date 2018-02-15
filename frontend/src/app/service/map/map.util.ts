import {Injectable} from '@angular/core';
import * as L from 'leaflet';
import 'proj4leaflet';
import {GeometryCollection} from '../../typings/geojson';
import {GeometryObject} from '../../typings/geojson';
import {Feature, FeatureCollection} from 'geojson';
import {MapFeatureInfo} from './map-feature-info';
import {ALLU_PREFIX} from './map-layer-id';

@Injectable()
export class MapUtil {

  private epsg3879: L.Proj.CRS;

  public featureCollectionToGeometryCollection(
    featureCollection: FeatureCollection<GeometryObject>): GeometryCollection {
    let geometryCollection: GeometryCollection;
    if (featureCollection && featureCollection.features) {
      const features = featureCollection.features;
      const geometries: GeometryObject[] = features.map(f => f.geometry);
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

  public geometryCollectionToFeatureCollection(geometryCollection: GeometryCollection, featureInfo?: MapFeatureInfo):
    FeatureCollection<GeometryObject> {
    let featureCollection;
    if (geometryCollection && geometryCollection.geometries) {
      const geometries: GeometryObject[] = geometryCollection.geometries;
      featureCollection = {
        type: 'FeatureCollection',
        features: geometries.map(g => this.createFeature(g, featureInfo))
      };
    }
    return featureCollection;
  }

  public featureToGeometry(feature: GeoJSON.Feature<GeometryObject>) {
    const geometry = this.createGeometry(feature);
    geometry.crs = {
      properties: {
        name: 'EPSG:3879'
      },
      type: 'name'
    };
    return geometry;
  }

  public polygonFromBounds(bounds: L.LatLngBounds): L.Rectangle {
    return L.rectangle(bounds);
  }

  constructor() {
    this.epsg3879 = this.createCrsEPSG3879();
  }

  get EPSG3879(): L.Proj.CRS {
    return this.epsg3879;
  }

  public wgs84ToEpsg3879(coordinate: Array<number>): Array<number> {
    const projected = this.EPSG3879.projection.project(L.latLng(coordinate[1], coordinate[0]));
    return [projected.x, projected.y];
  }

  public epsg3879ToWgs84(coordinate: Array<number>): Array<number> {
    const projected = this.EPSG3879.projection.unproject(L.point(coordinate[0], coordinate[1]));
    return [projected.lng, projected.lat];
  }

  private createCrsEPSG3879(): L.Proj.CRS {
    const crsName = 'EPSG:3879';
    const bounds = L.bounds([25440000, 6630000], [25571072, 6761072]);
    const projDef = '+proj=tmerc +lat_0=0 +lon_0=25 +k=1 +x_0=25500000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs';
    return new L.Proj.CRS(crsName, projDef, {
      resolutions: [256, 128, 64, 32, 16, 8, 4, 2, 1, 0.5, 0.25, 0.125, 0.0625, 0.03125],
      bounds: bounds
    });
  }

  private createFeature(geometry: GeometryObject, featureInfo?: MapFeatureInfo): GeoJSON.Feature<GeometryObject> {
    return {
      id: featureInfo ? `${ALLU_PREFIX}.${featureInfo.id}` : undefined,
      type: 'Feature',
      geometry: this.mapEPSG3879Geometry(geometry),
      properties: featureInfo
    };

  }

  private createGeometry(feature: GeoJSON.Feature<GeometryObject>): GeometryObject {
    return this.mapWgs84Geometry(feature.geometry);
  }

  private mapWgs84Geometry(geometry: any): any {
    return { type: geometry.type, coordinates: this.mapWgs84GeometryObject(geometry) };
  }

  private mapWgs84GeometryObject(geometry: any): any {
    switch (geometry.type) {
      case 'Point':
        return this.wgs84ToEpsg3879(geometry.coordinates);
      case 'LineString':
        return this.mapWgs84CoordinateArray(geometry.coordinates);
      default: {
        return this.mapWgs84GeometryArray(geometry.coordinates);
      }
    }
  }

  private mapWgs84GeometryArray(geometryArray: Array<Array<Array<number>>>): Array<Array<Array<number>>> {
    return geometryArray.map(ga => this.mapWgs84CoordinateArray(ga));
  }

  private mapWgs84CoordinateArray(coordinateArray: Array<Array<number>>): Array<Array<number>> {
    return coordinateArray.map(c => this.wgs84ToEpsg3879(c));
  }

  private mapEPSG3879Geometry(geometry: any): any {
    return { type: geometry.type, coordinates: this.mapEPSG3879GeometryObject(geometry) };
  }

  private mapEPSG3879GeometryObject(geometry: any): any {
    switch (geometry.type) {
      case 'Point':
        return this.epsg3879ToWgs84(geometry.coordinates);
      case 'LineString':
        return this.mapEPSG3879CoordinateArray(geometry.coordinates);
      default: {
        return this.mapEPSG3879GeometryArray(geometry.coordinates);
      }
    }
  }

  private mapEPSG3879GeometryArray(geometryArray: Array<Array<Array<number>>>): Array<Array<Array<number>>> {
    return geometryArray.map(ga => this.mapEPSG3879CoordinateArray(ga));
  }

  private mapEPSG3879CoordinateArray(coordinateArray: Array<Array<number>>): Array<Array<number>> {
    return coordinateArray.map(c => this.epsg3879ToWgs84(c));
  }
}
