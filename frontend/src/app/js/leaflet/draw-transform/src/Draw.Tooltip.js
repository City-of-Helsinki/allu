L.Draw.Tooltip = L.Draw.Tooltip.include({
  visible: true,

  hide: function() {
    this._updateVisibility(false);
    return this;
  },

  show: function() {
    this._updateVisibility(true);
    return this;
  },

  /**
   * Override to use visible flag
   */
  updatePosition: function (latlng) {
    const pos = this._map.latLngToLayerPoint(latlng);
    const tooltipContainer = this._container;

    if (tooltipContainer) {
      tooltipContainer.style.visibility = this._visibilityStyle();
      L.DomUtil.setPosition(tooltipContainer, pos);
    }

    return this;
  },

  _updateVisibility: function(isVisible) {
    this.visible = isVisible;
    if (this._container) {
      this._container.style.visibility = this._visibilityStyle();
    }
  },

  _visibilityStyle: function() {
    return this.visible ? 'inherit' : 'hidden';
  },
});