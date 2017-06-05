L.Handler.PathTransform = L.Handler.PathTransform.include({
  /**
   * Override to fix case where rotation is not allowed
   * @private
   */
  _updateHandlers: function() {
    var handlersGroup = this._handlersGroup;

    this._rectShape = this._rect.toGeoJSON();

    if (this._handleLine) {
      this._handlersGroup.removeLayer(this._handleLine);
    }

    if (this._rotationMarker) {
      this._handlersGroup.removeLayer(this._rotationMarker);
    }

    this._handleLine = this._rotationMarker = null;

    for (var i = this._handlers.length - 1; i >= 0; i--) {
      handlersGroup.removeLayer(this._handlers[i]);
    }

    this._createHandlers();
  },
});