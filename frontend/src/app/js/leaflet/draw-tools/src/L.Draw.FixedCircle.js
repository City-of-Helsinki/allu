export const MIN_RADIUS = 0.5; // In meters
export const DEFAULT_MIN_RADIUS = 1.0;

L.drawLocal.draw.handlers.circle.diameter = 'Diameter';

L.Draw.FixedCircle = L.Draw.Circle.extend({
  statics: {
    TYPE: 'fixedCircle'
  },

  options: {
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
    showRadius: true,
    metric: true, // Whether to use the metric measurement system or imperial
    feet: true, // When not metric, use feet instead of yards for display
    nautic: false // When not metric, not feet use nautic mile for display
  },

  _minRadius: DEFAULT_MIN_RADIUS,

  initialize: function (map, options) {
    L.Draw.Circle.prototype.initialize.call(this, map, options);
  },

  setMinDiameter: function(diameter) {
    var radius = diameter / 2;
    this._minRadius = radius >= MIN_RADIUS ? radius : MIN_RADIUS;
  },

  getMinDiameter: function() {
    return this._minRadius * 2;
  },

  _drawShape: function (latlng) {
    var distance;
    if (L.GeometryUtil.isVersion07x()) {
      distance = this._startLatLng.distanceTo(latlng);
    } else {
      distance = this._map.distance(this._startLatLng, latlng);
    }

    if (distance < this._minRadius) {
      distance = this._minRadius;
    }

    if (!this._shape) {
      this._shape = new L.Circle(this._startLatLng, distance, this.options.shapeOptions);
      this._map.addLayer(this._shape);
    } else {
      this._shape.setRadius(distance);
    }
  },

  _onMouseDown: function(e) {
    // Need to set map focus to fire min diameter input change
    this._map.getContainer().focus();
    L.Draw.SimpleShape.prototype._onMouseDown.call(this, e);
  },

  _onMouseUp: function() {
    if (!this._shape) {
      this._drawShape(this._startLatLng);
    }
    L.Draw.SimpleShape.prototype._onMouseUp.call(this);
  },

  _onMouseMove: function (e) {
    var latlng = e.latlng;

    this._tooltip.updatePosition(latlng);
    if (this._isDrawing) {
      this._drawShape(latlng);
      this._updateTooltip();
    }
  },

  _updateTooltip: function() {
    var showRadius = this.options.showRadius;
    var useMetric = this.options.metric;
    // Get the new diameter (rounded to 1 dp)
    var diameter = this._shape.getRadius() * 2;
    var fixedDiameter = diameter.toFixed(1);
    var subtext = '';

    if (showRadius) {
      subtext = L.drawLocal.draw.handlers.circle.diameter + ': ' +
        L.GeometryUtil.readableDistance(fixedDiameter, useMetric, this.options.feet, this.options.nautic);
    }
    this._tooltip.updateContent({
      text: this._endLabelText,
      subtext: subtext
    });
  }
});
