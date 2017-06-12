const turfIntersect = require('@turf/intersect');
const pip = require('@mapbox/leaflet-pip');

class Intersect {
  constructor(map) {
    this._map = map;
  }

  check(layer, againstLayer) {
    let intersecting = [];
    if (this.validLayers(layer, againstLayer)) {
      if (layer instanceof L.LatLng) {
        intersecting = pip.pointInLayer(layer, againstLayer);
      } else if((layer instanceof L.Polyline) || (layer instanceof L.Circle)) {
        againstLayer.eachLayer((l) => {
          intersecting = intersecting.concat(this.polyIntersect(layer, l));
        });
      }
    }
    return intersecting;
  }

  validLayers(l1, l2) {
    return l1 && l2 && l1._leaflet_id !== l2._leaflet_id;
  }

  polyIntersect(layer, againstLayer) {
    // Skip checking self
    if (layer._leaflet_id === againstLayer._leaflet_id) return [];

    const intersecting = turfIntersect(this.toGeoJSON(layer), this.toGeoJSON(againstLayer));
    return intersecting ? [againstLayer] : [];
  }

  toGeoJSON(layer) {
    if (layer instanceof L.Circle) {
      return layer.toPolygon().toGeoJSON();
    } else {
      return layer.toGeoJSON();
    }
  }
}


module.exports = Intersect;
