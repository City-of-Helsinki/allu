import * as L from 'leaflet';

declare module 'leaflet' {
  class WFSGeoJSON extends FeatureGroup {
  }

  function wfsGeoJSON(options: any): FeatureGroup;
}
