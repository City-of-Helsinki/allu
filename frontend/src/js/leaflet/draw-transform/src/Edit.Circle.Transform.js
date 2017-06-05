L.Edit.Circle.include( /** @lends L.Edit.Circle.prototype */ {

  /**
   * @override
   */
  _createMoveMarker: L.Edit.SimpleShape.prototype._createMoveMarker,

  _resize: function(latlng) {
    let center = this._shape.getLatLng();
    let radius = center.distanceTo(latlng);

    this._shape.setRadius(radius);

    this._updateMoveMarker();

    this._map.fire('draw:editresize', {layer: this._shape});
  },

  _superOnStopEditFeature: L.Edit.SimpleShape.prototype._onStopEditFeature,

  _onStopEditFeature: function() {
    this._superOnStopEditFeature();

    let center = this._shape.getLatLng();
    this._resizeMarkers[0].setLatLng(this._getResizeMarkerPoint(center));

    // show resize marker
    this._shape._map.addLayer(this._markerGroup);
    this._updateMoveMarker();
    this._fireEdit();
    this._shape.showMeasurements();
  }
});
