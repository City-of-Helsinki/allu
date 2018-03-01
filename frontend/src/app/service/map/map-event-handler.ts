import * as L from 'leaflet';
import inside from '@turf/boolean-point-in-polygon';

export class MapEventHandler {
  static clickIntersects(event: L.LeafletMouseEvent, map: L.Map, layers: any): L.Layer[] {
    const clickBounds = L.latLngBounds(event.latlng, event.latlng);
    const intersectingFeatures = [];

    for (const layer of layers) {
      if (map.hasLayer(layer)) {
        layer.eachLayer(feature => {
          const bounds = MapEventHandler.getBounds(feature);

          if (bounds && bounds.isValid() && clickBounds.intersects(bounds)) {
            const geoJson = feature.toGeoJSON();
            const coords = [event.latlng.lng, event.latlng.lat];
            if (inside(coords, geoJson)) {
              intersectingFeatures.push(feature);
            }
          }
        });
      }
    }
    return intersectingFeatures;
  }

  private static getBounds(feature: any): L.LatLngBounds {
    let bounds;
    if (feature.getBounds) {
      bounds = feature.getBounds();
    } else if (feature._latlng) {
      bounds = L.latLngBounds(feature._latlng, feature._latlng);
    }
    return bounds;
  }
}

