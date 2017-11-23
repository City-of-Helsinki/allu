declare namespace L {
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
