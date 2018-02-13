import {GeoJsonGeometryTypes, GeoJsonTypes} from 'geojson';

export interface GeoJsonCRS {
  properties: {name: string};
  type: string;
}

export interface GeoJsonObject extends GeoJSON.GeoJsonObject {
  type: GeoJsonTypes;
  crs?: GeoJsonCRS;
}

export interface GeometryObject extends GeoJsonObject, GeoJSON.GeometryObject {
  type: GeoJsonGeometryTypes;
}

export interface GeometryCollection extends GeoJsonObject, GeoJSON.GeometryCollection {
  type: 'GeometryCollection';
}

