const buffer = require('@turf/buffer');

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

  _width: 1.0,
  _bufferedLine: undefined,
  _polyShapeOptions: undefined, // For easier access

  initialize: function (map, options) {
    this.type = L.Draw.BufferPolyLine.TYPE;
    L.Draw.Polyline.prototype.initialize.call(this, map, options);
    this._polyShapeOptions = options.polyOptions.shapeOptions;
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

  addVertex(latlng) {
    L.Draw.Polyline.prototype.addVertex.call(this, latlng);
    this._updateBufferedLine();
  },

  deleteLastVertex() {
    L.Draw.Polyline.prototype.deleteLastVertex.call(this);
    this._updateBufferedLine();
  },

  setWidth(width) {
    this._width = width > 0 ? width : 0.1;
    this._updateBufferedLine();
  },

  _updateBufferedLine() {
    if (this._bufferedLine) {
      this._map.removeLayer(this._bufferedLine);
    }

    if (this._poly.getLatLngs().length > 1) {
      const bufferedGeoJSON = buffer(this._poly.toGeoJSON(), this._toRadius(this._width));
      // It is safe to assume only single polygon since it is created from single continuous line
      this._bufferedLine = L.geoJSON(bufferedGeoJSON, this._polyShapeOptions).getLayers()[0];
      this._map.addLayer(this._bufferedLine);
    }
  },

  _toRadius(inMeters) {
    return inMeters / 1000 / 2; // width to kilometers to radius
  },

  _fireCreatedEvent: function () {
    const poly = new this.Poly(this._bufferedLine.getLatLngs(), this._polyShapeOptions);
    L.Draw.Feature.prototype._fireCreatedEvent.call(this, poly);
    this._map.removeLayer(this._bufferedLine);
  }
});
