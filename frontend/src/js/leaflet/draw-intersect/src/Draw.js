const Intersect = require('./Intersect');

L.Draw.Event.INTERSECTS = 'draw:intersects';

L.Control.Draw.include({
  _onAdd: L.Control.Draw.prototype.onAdd,

  onAdd: function (map) {
    this._map = map;
    this._intersect = new Intersect(map);
    this._initIntersectChecking();
    return this._onAdd(map);
  },

  _onRemove: L.Control.Draw.prototype.onAdd,

  onRemove: function() {
    this._disableIntersectChecking();
    this._onRemove();
  },

  _initIntersectChecking() {
    this._map
      .on(L.Draw.Event.DRAWVERTEX, this._onDrawVertex, this)
      .on(L.Draw.Event.CREATED, this._onCreated, this)
      .on(L.Draw.Event.EDITED, this._onEditVertex, this)
      .on(L.Draw.Event.EDITVERTEX, this._onEditVertex, this);
  },

  _disableIntersectChecking() {
    this._map
      .off(L.Draw.Event.DRAWVERTEX, this._onDrawVertex, this)
      .off(L.Draw.Event.CREATED, this._onCreated, this)
      .off(L.Draw.Event.EDITED, this._onEditVertex, this)
      .off(L.Draw.Event.EDITVERTEX, this._onEditVertex, this);
  },

  // Need to handle vertex drawing as separate case since currently drawn vertices
  // can be interpreted as a point (1 point), a line (2 points) or a polygon (3 or more points)
  _onDrawVertex(evt) {
    let latLngs = [];
    evt.layers.eachLayer(l => {
      latLngs.push(l._latlng);
    });

    if (latLngs.length > 2) {
      this._intersects(L.polygon(latLngs));
    } else if (latLngs.length > 1) {
      this._intersects(L.polyline(latLngs));
    } else if (latLngs.length > 0) {
      let latlng = latLngs[0];
      this._intersects(L.latLng(latlng.lat, latlng.lng));
    }
  },

  _onCreated(evt) {
    this._intersects(evt.layer);
  },

  _onEditVertex(evt) {
    this._intersects(evt.poly);
  },

  _intersects(layer) {
    try {
      const intersecting = this._intersectsLayers(layer);
      if (intersecting.length) {
        this._map.fire(L.Draw.Event.INTERSECTS, intersecting[0]);
      }
    } catch(error) {
      console.log('Error checking intersect', error);
    }
  },

  _intersectsLayers(layer) {
    let intersecting = [];
    this._intersectLayers.some(il => {
      intersecting = this._intersect.check(layer, il);
      // Return true when first intersection is found so some terminates
      return intersecting.length;
    });
    return intersecting;
  }
});

L.Control.Draw.addInitHook(function() {
  const editLayers = [this.options.edit.featureGroup];
  const intersectLayers = this.options.intersectLayers || [];
  this._intersectLayers = editLayers.concat(intersectLayers);
});
