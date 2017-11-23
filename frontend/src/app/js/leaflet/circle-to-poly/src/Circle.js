const DOUBLE_PI = Math.PI * 2;

L.Circle.SECTIONS_COUNT = 64;

L.Circle.include({

  /**
   * Static
   * @param  {L.Circle} original circle
   * @param  {Number?}  number of vertices used for creating polygon circle
   * @param  {L.Map?}   map
   * @return {L.Polygon} polygon circle
   */
  toPolygon:  function(vertices, map) {
    map = map || this._map;
    if (!map) {
      throw Error("Can't figure out points without adding the feature to the map");
    }

    const points = [];
    const crs = map.options.crs;
    let radius;
    let project;
    let unproject;

    if (crs === L.CRS.EPSG3857) {
      project = map.latLngToLayerPoint.bind(map);
      unproject = map.layerPointToLatLng.bind(map);
      radius = this._radius;
    } else { // especially if we are using Proj4Leaflet
      project = crs.projection.project.bind(crs.projection);
      unproject = crs.projection.unproject.bind(crs.projection);
      radius = this._mRadius;
    }

    let projectedCentroid = project(this._latlng)
    let angle = 0.0;

    vertices = vertices || L.Circle.SECTIONS_COUNT;
    let point;
    for (var i = 0; i < vertices - 1; i++) {
      angle -= (DOUBLE_PI / vertices); // clockwise
      point = new L.Point(
        projectedCentroid.x + (radius * Math.cos(angle)),
        projectedCentroid.y + (radius * Math.sin(angle))
      );

      if (i === 0 || !point.equals(points[i - 1])) {
        points.push(unproject(point));
      }
    }

    return L.polygon(points);
  }
});
