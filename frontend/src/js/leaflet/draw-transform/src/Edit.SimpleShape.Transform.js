const Options =  require('./Options');

/**
 * Mainly central marker routines
 */

L.Edit.SimpleShape.include( /** @lends L.Edit.SimpleShape.prototype */ {

  /**
   * @override
   */
  addHooks: function() {
    let shape = this._shape;
    if (this._shape._map) {
      this._map = this._shape._map;
      shape.setStyle(shape.options.editing);

      if (shape._map) {
        this._map = shape._map;
        if (!this._markerGroup) {
          this._enableDragging();
          this._enableTransform();
          this._initMarkers();
        }
        this._map.addLayer(this._markerGroup);
      }
    }
  },

  /**
   * @override
   */
  removeHooks: function() {
    let shape = this._shape;

    shape.setStyle(shape.options.original);

    if (shape._map) {
      if (this._moveMarker) {
        this._unbindMarker(this._moveMarker);
      }

      for (var i = 0, l = this._resizeMarkers.length; i < l; i++) {
        this._unbindMarker(this._resizeMarkers[i]);
      }
      this._resizeMarkers = null;
      this._disableDragging();
      this._disableTransform();

      this._map.removeLayer(this._markerGroup);
      delete this._markerGroup;
      delete this._markers;
    }

    this._map = null;
  },

  /**
   * Adds drag start listeners
   */
  _enableDragging: function() {
    if (!this._shape.dragging) {
      this._shape.dragging = new L.Handler.PathDrag(this._shape);
    }
    this._shape.dragging.enable();
    this._shape
      .on('dragstart', this._onStartEditFeature, this);
  },

  /**
   * Removes drag start listeners
   */
  _disableDragging: function() {
    this._shape.dragging.disable();
    this._shape
      .off('dragstart', this._onStartTransformFeature, this);
  },

  _enableTransform: function(options) {
    options = options || Options.DEFAULT_TRANSFORM_OPTIONS;
    if (!this._shape.transform) {
      this._shape.transform = new L.Handler.PathTransform(this._shape);
    }

    this._shape.transform.enable(options);

    this._shape
      .on('transformstart', this._onStartTransformFeature, this)
      .on('transformed', this._onStopTransformFeature, this);
  },

  _disableTransform: function() {
    this._shape.transform.disable();
    this._shape.transform = undefined;
    this._shape
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
    this._shape.hideMeasurements();
    this._map._editTooltip.hide();
    if (this._markerGroup) {
      this._shape._map.removeLayer(this._markerGroup);
    }
    this._shape.fire('editstart');
  },

  _onStopEditFeature: function(evt) {
    this._map._editTooltip.hide();
  }, // Derived components should implement this

  /**
   * Put move marker into center
   */
  _updateMoveMarker: function() {
    if (this._moveMarker) {
      this._moveMarker.setLatLng(this._getShapeCenter());
    }
  },

  /**
   * Shape centroid
   * @return {L.LatLng}
   */
  _getShapeCenter: function() {
    return this._shape.getBounds().getCenter();
  },

  /**
   * @override
   */
  _createMoveMarker: function() {
    if (L.EditToolbar.Edit.MOVE_MARKERS) {
      this._moveMarker = this._createMarker(this._getShapeCenter(), this.options.moveIcon);
    }
  },

  __fireEdit: L.Edit.SimpleShape.prototype._fireEdit,

  _fireEdit: function () {
    this.__fireEdit();
    this._shape._map.fire(L.Draw.Event.EDITVERTEX, { layers: this._markerGroup, poly: this._shape });
  }
});

/**
 * Override this if you don't want the central marker
 * @type {Boolean}
 */
L.Edit.SimpleShape.mergeOptions({
  moveMarker: false
});
