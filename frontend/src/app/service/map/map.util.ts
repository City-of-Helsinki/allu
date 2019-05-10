import {Injectable} from '@angular/core';
import * as L from 'leaflet';
import {MapFeatureInfo} from './map-feature-info';
import {ALLU_PREFIX} from './map-layer-id';
import {DirectGeometryObject, Feature, FeatureCollection, GeometryCollection, GeometryObject, MultiPolygon} from 'geojson';
import area from '@turf/area';
import {Some} from '@util/option';
import {Projection} from '@feature/map/projection';

function isDirectGeometryObject(geometryObject: GeometryObject): geometryObject is DirectGeometryObject {
  return geometryObject !== undefined && (<DirectGeometryObject>geometryObject).coordinates !== undefined;
}

function isMultiPolygon(geometryObject: GeometryObject): geometryObject is MultiPolygon {
  return isDirectGeometryObject(geometryObject) && geometryObject.type === 'MultiPolygon';
}

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
        geometries: geometries.map(g => this.project(g))
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

  public unprojectFeatureCollection(featureCollection: FeatureCollection<GeometryObject>): FeatureCollection<GeometryObject> {
    return {
      ...featureCollection,
      features: featureCollection.features.map(feature => this.unprojectFeature(feature))
    };
  }

  /**
   * Leaflet Draw cannot handle MultiPolygons so they are transformed as multiple Polygons
   */
  public sanitizeForLeaflet(featureCollection: FeatureCollection<GeometryObject>): FeatureCollection<GeometryObject> {
    const sanitized = featureCollection.features
      .map(feature => this.sanitizeFeatureForLeaflet(feature))
      .reduce((acc, cur) => acc.concat(cur), []);
    return this.wrapToFeatureCollection(sanitized);
  }

  private createFeature(geometry: GeometryObject, featureInfo?: MapFeatureInfo): Feature<GeometryObject> {
    return {
      id: featureInfo ? `${ALLU_PREFIX}.${featureInfo.id}` : undefined,
      type: 'Feature',
      geometry: this.unproject(geometry),
      properties: featureInfo
    };

  }

  private unprojectFeature(feature: Feature<GeometryObject>): Feature<GeometryObject> {
    if (isDirectGeometryObject(feature.geometry)) {
      return {
        ...feature,
        geometry: this.unproject(feature.geometry)
      };
    } else {
      return feature;
    }
  }


  private createGeometry(feature: Feature<GeometryObject>): GeometryObject {
    return this.project(feature.geometry);
  }

  private project(geometry: any): any {
    return {
      type: geometry.type,
      coordinates: this.projection.project(geometry.coordinates)
    };
  }

  private unproject(geometry: any): any {
    return {
      type: geometry.type,
      coordinates: this.projection.unproject(geometry.coordinates)
    };
  }

  private sanitizeFeatureForLeaflet(feature: Feature<GeometryObject>): Feature<GeometryObject>[] {
    return this.sanitizeGeometryObjectForLeaflet(feature.geometry).map(geometry => ({
      ...feature,
      geometry
    }));
  }

  private sanitizeGeometryObjectForLeaflet(geometryObject: GeometryObject): GeometryObject[] {
    if (isMultiPolygon(geometryObject)) {
      return geometryObject.coordinates.map(coordinates => ({type: 'Polygon', coordinates}));
    } else {
      return [geometryObject];
    }
  }
}
