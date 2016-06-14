import {BackendLocation} from '../backend-model/backend-location';
import {Location} from '../../model/common/location';

export class LocationMapper {
  public static mapBackend(backendLocation: BackendLocation): Location {
    console.log('LocationMapper.mapBackend', backendLocation);
    return (backendLocation) ?
      new Location(
        backendLocation.id,
        LocationMapper.geometryCollectionToFeatureCollection(backendLocation.geometry),
        backendLocation.postalAddress) : undefined;
  }
  public static mapFrontend(location: Location): BackendLocation {
    return (location) ?
    {
      id: location.id,
      geometry: LocationMapper.featureCollectionToGeometryCollection(location.geometry),
      postalAddress: location.postalAddress
    } : undefined;
  }

  private static featureCollectionToGeometryCollection(
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
        geometries: geometries
      };
    }
    return geometryCollection;
  }

  private static geometryCollectionToFeatureCollection(
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

  private static createFeature(geometry: GeoJSON.GeometryObject, properties: any): GeoJSON.Feature<GeoJSON.GeometryObject> {
    return {
      type: 'Feature',
      geometry: geometry,
      properties: undefined
    };
  }
}
