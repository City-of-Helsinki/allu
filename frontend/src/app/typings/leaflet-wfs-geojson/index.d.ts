// eslint-disable-next-line @typescript-eslint/no-unused-vars -- required to keep this file in module context so the 'leaflet' module augmentation resolves leaflet types
import * as L from 'leaflet';

declare module 'leaflet' {
  class WFSGeoJSON extends FeatureGroup {
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- leaflet typings interop
  function wfsGeoJSON(options: any): FeatureGroup;
}
