L.Draw.Tooltip = L.Draw.Tooltip.include({

  visibility: 'inherit',

  hide: function() {
    this._updateVisibility('hidden');
  },

  show: function() {
    this._updateVisibility('inherit');
  },

  /**
   * Override to use visible flag
   */
  updatePosition: function (latlng) {
    var pos = this._map.latLngToLayerPoint(latlng),
      tooltipContainer = this._container;

    if (this._container) {
      tooltipContainer.style.visibility = this.visibility;
      L.DomUtil.setPosition(tooltipContainer, pos);
    }

    return this;
  },

  _updateVisibility: function(visibility) {
    this.visibility = visibility;
    if (this._container) {
      this._container.style.visibility = visibility;
    }
  },
});