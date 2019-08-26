import * as L from 'leaflet';
import inside from '@turf/boolean-point-in-polygon';
import {Feature, GeometryObject} from 'geojson';

export class MapEventHandler {
  static clickIntersects(event: L.LeafletMouseEvent, map: L.Map, layers: any): Feature<GeometryObject>[] {
    const clickBounds = L.latLngBounds(event.latlng, event.latlng);
    const intersectingFeatures = [];

    for (const layer of layers) {
      if (map.hasLayer(layer)) {
        layer.eachLayer(featureGroup => {
          const bounds = MapEventHandler.getBounds(featureGroup);

          if (bounds && bounds.isValid() && clickBounds.intersects(bounds)) {
            const feature = featureGroup.toGeoJSON();
            const coords = [event.latlng.lng, event.latlng.lat];
            if (inside(coords, feature)) {
              intersectingFeatures.push(feature);
            }
          }
        });
      }
    }
    return intersectingFeatures;
  }

  private static getBounds(feature: any): L.LatLngBounds {
    return feature.getBounds ? feature.getBounds() : undefined;
  }
}

