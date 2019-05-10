import {Injectable} from '@angular/core';
import * as L from 'leaflet';
import {MapFeatureInfo} from './map-feature-info';
import {ALLU_PREFIX} from './map-layer-id';
import {Feature, FeatureCollection, GeometryCollection, GeometryObject} from 'geojson';
import area from '@turf/area';
import {Some} from '@util/option';
import {Projection} from '@feature/map/projection';

@Injectable()
export class MapUtil {

  constructor(private projection: Projection) {
  }

  public static geometryCount(geometryCollection: GeometryCollection): number {
    return geometryCollection
      ? geometryCollection.geometries.length
      : 0;
  }

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

  public createFeatureCollection(geometryCollection?: GeometryCollection, featureInfo?: MapFeatureInfo):
    FeatureCollection<GeometryObject> {
    const features = Some(geometryCollection)
      .map(gc => gc.geometries)
      .map(geometries => geometries.map(g => this.createFeature(g, featureInfo)))
      .orElse([]);

    return this.wrapToFeatureCollection(features);
  }

  public wrapToFeatureCollection(features: Feature<GeometryObject>[]): FeatureCollection<GeometryObject> {
    return {
      type: 'FeatureCollection',
      features: features
    };
  }

  public mergeFeatureCollections(featureCollections: FeatureCollection<GeometryObject>[] = []): FeatureCollection<GeometryObject> {
    const features = featureCollections.reduce((acc, cur) => acc.concat(cur.features), []);
    return {
      type: 'FeatureCollection',
      features: features
    };
  }

  public featureToGeometry(feature: Feature<GeometryObject>) {
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

  public isValidGeometry(layer: any): boolean {
    if (layer instanceof L.Circle || layer instanceof L.Point) {
      return true;
    } else {
      const geoJSON = layer.toGeoJSON();
      return area(geoJSON) > 0;
    }
  }

  private createFeature(geometry: GeometryObject, featureInfo?: MapFeatureInfo): Feature<GeometryObject> {
    return {
      id: featureInfo ? `${ALLU_PREFIX}.${featureInfo.id}` : undefined,
      type: 'Feature',
      geometry: this.mapEPSG3879Geometry(geometry),
      properties: featureInfo
    };

  }

  private createGeometry(feature: Feature<GeometryObject>): GeometryObject {
    return this.mapWgs84Geometry(feature.geometry);
  }

  private mapWgs84Geometry(geometry: any): any {
    return { type: geometry.type, coordinates: this.mapWgs84GeometryObject(geometry) };
  }

  private mapWgs84GeometryObject(geometry: any): any {
    return this.projection.project(geometry.coordinates);
  }

  private mapEPSG3879Geometry(geometry: any): any {
    return { type: geometry.type, coordinates: this.mapEPSG3879GeometryObject(geometry) };
  }

  private mapEPSG3879GeometryObject(geometry: any): any {
    return this.projection.unproject(geometry.coordinates);
  }
}
