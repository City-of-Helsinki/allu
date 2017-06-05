/**
 * Dragging routines for poly handler
 */
L.Edit.Rectangle.include( /** @lends L.Edit.Rectangle.prototype */ {

  /**
   * @override
   */
  _createMoveMarker: L.Edit.SimpleShape.prototype._createMoveMarker,

  /**
   * @override
   */
  _resize: function(latlng) {
    // Update the shape based on the current position of
    // this corner and the opposite point
    this._shape.setBounds(L.latLngBounds(latlng, this._oppositeCorner));
    this._updateMoveMarker();

    this._shape._map.fire('draw:editresize', { layer: this._shape });
  },

  /**
   * @override
   */
  _onMarkerDragEnd: function(e) {
    this._toggleCornerMarkers(1);
    this._repositionCornerMarkers();

    L.Edit.SimpleShape.prototype._onMarkerDragEnd.call(this, e);
  },

  _superOnStopEditFeature: L.Edit.SimpleShape.prototype._onStopEditFeature,

  _onStopEditFeature: function() {
    this._superOnStopEditFeature();

    let polygon = this._shape;
    for (let j = 0, jj = polygon._latlngs.length; j < jj; j++) {
      for (let i = 0, len = polygon._latlngs[j].length; i < len; i++) {
        // update marker
        let marker = this._resizeMarkers[i];
        marker.setLatLng(polygon._latlngs[j][i]);

        // this one's needed to update the path
        marker._origLatLng = polygon._latlngs[j][i];
        if (marker._middleLeft) {
          marker._middleLeft.setLatLng(this._getMiddleLatLng(marker._prev, marker));
        }
        if (marker._middleRight) {
          marker._middleRight.setLatLng(this._getMiddleLatLng(marker, marker._next));
        }
      }
    }

    // show vertices
    this._shape._map.addLayer(this._markerGroup);
    this._updateMoveMarker();

    this._repositionCornerMarkers();
    this._fireEdit();
    this._shape.showMeasurements();
  }
});
