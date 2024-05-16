const intersect = require('@turf/intersect').default;
const pip = require('../../leaflet-pip/leaflet-pip');

function Intersect (map) {
  this._map = map;
}

Intersect.prototype.check = function(layer, againstLayer) {
  let intersecting = [];
  if (this.validLayers(layer, againstLayer)) {
    if (layer instanceof L.LatLng) {
      intersecting = pip.pointInLayer(layer, againstLayer);
    } else if((layer instanceof L.Polyline) || (layer instanceof L.Circle)) {
      const self = this;
      againstLayer.eachLayer(function(l) {
        intersecting = intersecting.concat(self.polyIntersect(layer, l));
      });
    }
  }
  return intersecting;
};

Intersect.prototype.validLayers = function(l1, l2) {
  if (l1 && l2) {
    const comparedOnMap = !!l2._map;
    const differentLayers = l1._leaflet_id !== l2._leaflet_id;
    return comparedOnMap && differentLayers;
  } else {
    return false;
  }
}

Intersect.prototype.polyIntersect = function(layer, againstLayer) {
  // Skip checking self
  if (layer._leaflet_id === againstLayer._leaflet_id) return [];

  try {
    const intersecting = intersect(this.toGeoJSON(layer), this.toGeoJSON(againstLayer));
    return intersecting ? [againstLayer] : [];
  } catch (e) {
    return [];
  }
}

Intersect.prototype.toGeoJSON = function(layer) {
  if (layer instanceof L.Circle) {
    return layer.toPolygon().toGeoJSON();
  } else {
    return layer.toGeoJSON();
  }
}


module.exports = Intersect;
