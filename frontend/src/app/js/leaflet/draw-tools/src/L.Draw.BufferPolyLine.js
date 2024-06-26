import buffer from './buffer';

export const MIN_WIDTH = 0.2;

L.Draw.BufferPolyLine = L.Draw.Polyline.extend({
  statics: {
    TYPE: 'bufferPolyline'
  },

  Poly: L.Polygon,

  options: {
    shapeOptions: {
      stroke: true,
      color: '#3388ff',
      weight: 4,
      opacity: 0.5,
      fill: false,
      clickable: true
    },
    polyOptions: {
      showArea: false,
      shapeOptions: {
        stroke: true,
        color: '#3388ff',
        weight: 4,
        opacity: 0.5,
        fill: true,
        fillColor: null, //same as color by default
        fillOpacity: 0.2,
        clickable: true
      },
      metric: true // Whether to use the metric measurement system or imperial
    }
  },

  _width: 2.0,
  _bufferedLine: undefined,
  _polyShapeOptions: undefined, // For easier access

  initialize: function(map, options) {
    this.type = L.Draw.BufferPolyLine.TYPE;
    L.Draw.Polyline.prototype.initialize.call(this, map, options);
    this._polyShapeOptions = options.polyOptions.shapeOptions;
    this._bufferOptions = {steps: 4, units: 'meters'};
  },

  // @method removeHooks(): void
  // Remove listener hooks from this handler.
  removeHooks: function () {
    L.Draw.Polyline.prototype.removeHooks.call(this);

    if (this._bufferedLine) {
      this._map.removeLayer(this._bufferedLine);
      delete this._bufferedLine;
    }
  },

  addVertex: function(latlng) {
    L.Draw.Polyline.prototype.addVertex.call(this, latlng);
    this._updateBufferedLine();
  },

  deleteLastVertex: function() {
    L.Draw.Polyline.prototype.deleteLastVertex.call(this);
    this._updateBufferedLine();
  },

  setWidth: function(width) {
    this._width = width >= MIN_WIDTH ? width : MIN_WIDTH;
    this._updateBufferedLine();
  },

  _updateBufferedLine: function() {
    if (this._bufferedLine) {
      this._map.removeLayer(this._bufferedLine);
    }

    if (this._poly.getLatLngs().length > 1) {
      const bufferedGeoJSON = buffer(this._poly.toGeoJSON(), this._toRadius(this._width), this._bufferOptions);
      // It is safe to assume only single polygon since it is created from single continuous line
      const layer = L.geoJSON(bufferedGeoJSON).getLayers()[0];
      this._bufferedLine = L.polygon(layer.getLatLngs(), this._polyShapeOptions);
      this._map.addLayer(this._bufferedLine);
    }
  },

  _toRadius: function(diameterInMeters) {
    return diameterInMeters / 2; // diameter to radius
  },

  _fireCreatedEvent: function () {
    const poly = new this.Poly(this._bufferedLine.getLatLngs(), this._polyShapeOptions);
    L.Draw.Feature.prototype._fireCreatedEvent.call(this, poly);
    this._map.removeLayer(this._bufferedLine);
  }
});
