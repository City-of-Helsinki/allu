const Options =  require('./Options');

L.Edit.PolyVerticesEdit.include( /** @lends L.Edit.PolyVerticesEdit.prototype */ {

  // store methods to call them in overrides
  __createMarker: L.Edit.PolyVerticesEdit.prototype._createMarker,
  __removeMarker: L.Edit.PolyVerticesEdit.prototype._removeMarker,

  /**
   * @override
   */
  // @method addHooks(): void
  // Add listener hooks to this handler.
  addHooks: function () {
    var poly = this._poly;

    if (!(poly instanceof L.Polygon)) {
      poly.options.fill = false;
      if (poly.options.editing) {
        poly.options.editing.fill = false;
      }
    }

    poly.setStyle(poly.options.editing);

    if (this._poly._map) {

      this._map = this._poly._map; // Set map

      if (!this._markerGroup) {
        this._enableDragHandlers();
        this._enableDragging();
        this._enableTransform();
        this._initMarkers();
        this._createMoveMarker();
      }
      this._poly._map.addLayer(this._markerGroup);
    }
  },

  /**
   * @override
   */
  removeHooks: function() {
    let poly = this._poly;

    poly.setStyle(poly.options.original);
    if (this._poly._map) {
      this._poly._map.removeLayer(this._markerGroup);
      this._disableDragHandlers();
      this._disableDragging();
      this._disableTransform();
      delete this._markerGroup;
      delete this._markers;
    }
    this._map = null;
  },

  /**
   * @override
   */
  _createMoveMarker: function() {
    if (L.EditToolbar.Edit.MOVE_MARKERS && (this._poly instanceof L.Polygon)) {
      this._moveMarker = new L.Marker(this._getShapeCenter(), {
        icon: this.options.moveIcon
      });
      this._moveMarker.on('mousedown', this._delegateToShape, this);
      this._markerGroup.addLayer(this._moveMarker);
    }
  },

  /**
   * Start dragging through the marker
   * @param  {L.MouseEvent} evt
   */
  _delegateToShape: function(evt) {
    var poly = this._shape || this._poly;
    var marker = evt.target;
    poly.fire('mousedown', L.Util.extend(evt, {
      containerPoint: L.DomUtil.getPosition(marker._icon)
        .add(poly._map._getMapPanePos())
    }));
  },

  /**
   * Polygon centroid
   * @return {L.LatLng}
   */
  _getShapeCenter: function() {
    return this._poly.getCenter();
  },

  _enableDragHandlers: function() {
    this._poly
      .on('editdrag', this._onVertexDrag, this);
  },

  _disableDragHandlers: function() {
    this._poly
      .off('editdrag', this._onVertexDrag, this);
  },

  /**
   * Adds drag start listeners
   */
  _enableDragging: function() {
    if (!this._poly.dragging) {
      this._poly.dragging = new L.Handler.PathDrag(this._poly);
    }
    this._poly.dragging.enable();
    this._poly
      .on('dragstart', this._onStartTransformFeature, this);
  },

  /**
   * Removes drag start listeners
   */
  _disableDragging: function() {
    this._poly.dragging.disable();
    this._poly
      .off('dragstart', this._onStartTransformFeature, this);
  },

  _enableTransform: function(options) {
    options = options || Options.DEFAULT_TRANSFORM_OPTIONS;
    if (!this._poly.transform) {
      this._poly.transform = new L.Handler.PathTransform(this._poly);
    }

    this._poly.transform.enable(options);

    this._poly
      .on('transformstart', this._onStartTransformFeature, this)
      .on('transformed', this._onStopTransformFeature, this);
  },

  _disableTransform: function() {
    this._poly.transform.disable();
    this._poly.transform = undefined;
    this._poly
      .off('transformstart', this._onStartTransformFeature, this)
      .off('transformed', this._onStopTransformFeature, this);
  },

  _onStartTransformFeature: function(evt) {
    this._onStartEditFeature(evt);
  },

  _onStopTransformFeature: function(evt) {
    this._onStopEditFeature(evt);
  },

  _onStartEditFeature: function(evt) {
    this._poly.hideMeasurements();
    this._map._editTooltip.hide();
    this._poly._map.removeLayer(this._markerGroup);
    this._poly.fire('editstart');
  },

  _onStopEditFeature: function(evt) {
    let polygon = this._poly;
    this._updateMarkers(polygon._latlngs);

    // show vertices
    this._poly._map.addLayer(this._markerGroup);
    this._updateMoveMarker(this);
    this._map._editTooltip.show();
    this._poly.showMeasurements();
    this._fireEdit();
  },

  _updateMarkers: function(latLngs) {
    // We might have multipolygon since we have array inside array.
    // Need to update markers recursively for them
    if (latLngs.some(function(sub) { return sub.length; })) {
      latLngs.forEach(function(sub) { this._updateMarkers(sub); });
    } else {
      for (let i = 0, len = latLngs.length; i < len; i++) {
        // update marker
        let marker = this._markers[i];
        marker.setLatLng(latLngs[i]);

        // this one's needed to update the path
        marker._origLatLng = latLngs[i];
        if (marker._middleLeft) {
          marker._middleLeft.setLatLng(this._getMiddleLatLng(marker._prev, marker));
        }
        if (marker._middleRight) {
          marker._middleRight.setLatLng(this._getMiddleLatLng(marker, marker._next));
        }
      }
    }
  },

  _onVertexDrag: function(evt) {
    this._updateMeasurements();
  },

  _updateMeasurements: function() {
    try {
      this._poly.updateMeasurements();
    } catch (e) {
      // updating measurements throws sometimes error which we can do nothing about so ignore it
    }
  },

  /**
   * Copy from simple shape
   */
  _updateMoveMarker: L.Edit.SimpleShape.prototype._updateMoveMarker,

  /**
   * @override
   */
  _createMarker: function(latlng, index) {
    var marker = this.__createMarker(latlng, index);
    marker
      .on('dragstart', this._hideMoveMarker, this)
      .on('dragend', this._showUpdateMoveMarker, this);
    return marker;
  },

  /**
   * @override
   */
  _removeMarker: function(marker) {
    this.__removeMarker(marker);
    marker
      .off('dragstart', this._hideMoveMarker, this)
      .off('dragend', this._showUpdateMoveMarker, this);
  },

  /**
   * Hide move marker while dragging a vertex
   */
  _hideMoveMarker: function() {
    if (this._moveMarker) {
      this._markerGroup.removeLayer(this._moveMarker);
    }
  },

  /**
   * Show and update move marker
   */
  _showUpdateMoveMarker: function() {
    if (this._moveMarker) {
      this._markerGroup.addLayer(this._moveMarker);
      this._updateMoveMarker();
    }
  }
});

/**
 * @type {L.DivIcon}
 */
L.Edit.PolyVerticesEdit.prototype.options.moveIcon = new L.DivIcon({
  iconSize: new L.Point(8, 8),
  className: 'leaflet-div-icon leaflet-editing-icon leaflet-edit-move'
});

/**
 * Override this if you don't want the central marker
 * @type {Boolean}
 */
L.Edit.PolyVerticesEdit.mergeOptions({
  moveMarker: false
});
