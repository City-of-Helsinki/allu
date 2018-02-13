import * as L from 'leaflet';

declare module 'leaflet' {
  namespace Control {
    interface DrawConstructorOptions {
      intersectLayers: L.FeatureGroup[];
    }
  }

  namespace Draw {
    namespace Event {
      const INTERSECTS: string;
    }
  }
}
