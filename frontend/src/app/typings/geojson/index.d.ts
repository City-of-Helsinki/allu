import * as GeoJSON from 'geojson';

declare module 'geojson' {
  interface GeoJsonCRS {
    properties: {name: string};
    type: string;
  }

  interface GeoJsonObject {
    type: GeoJsonTypes;
    crs?: GeoJsonCRS;
  }

  interface GeometryObject {
    type: GeoJsonGeometryTypes;
  }

  interface GeometryCollection {
    type: 'GeometryCollection';
  }
}



