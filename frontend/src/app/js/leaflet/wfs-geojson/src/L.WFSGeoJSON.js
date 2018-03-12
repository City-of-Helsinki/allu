'use strict';

const request = require('superagent');

L.WFSGeoJSON = L.FeatureGroup.extend({
  options: {
    crs: L.CRS.EPSG3857,
    url: '',
    version: '1.1.0',
    typeName: undefined,
    opacity: 1,
    style: {
      color: 'black',
      weight: 1,
      opacity: 1,
      fillOpacity: 1
    }
  },

  _params: {
    service: 'WFS',
    request: 'GetFeature',
    outputFormat: 'application/json'
  },

  timeoutOptions: {
    response: 10000,  // Wait 10 seconds for the server to start sending,
    deadline: 60000, // but allow 1 minute for the file to finish loading.
  },

  initialize: function (options) {
    L.setOptions(this, options);
    this._params.version = this.options.version;
    this._params.typeName = this.options.typeName;

    this._layers = {};

    this.setStyle(this.options.style);
    this.loadFeatures();
  },

  loadFeatures: function () {
    this.clearLayers();
    var onSuccess = this._onSuccess.bind(this);
    var onError = this._onError.bind(this);

    this._fetch({
      url: this.options.url,
      headers: this.options.headers || {},
      params: this._params,
      success: onSuccess,
      error: onError
    });
  },

  _fetch: function(options) {
    options = L.extend({
      async: true,
      method: 'POST',
      data: '',
      params: {},
      headers: {},
      url: window.location.href,
      success: function (data) {
        console.log(data);
      },
      error: function (data) {
        console.log('Request failed');
        console.log(data);
      },
      complete: function () {
      }
    }, options);

    request.get(options.url)
      .query(options.params)
      .set(options.headers)
      .timeout(this.timeoutOptions)
      .then(options.success, options.error);
  },

  _onSuccess: function(response) {
    const content = JSON.parse(response.text);
    var coordsToLatLng = this._coordsToLatLng.bind(this);

    var layers = L.geoJSON(content, {
      coordsToLatLng: coordsToLatLng,
      style: this.options.style
    });

    var addLayer = this.addLayer.bind(this);
    layers.eachLayer(addLayer);

    this.fire('load', { layers: layers });

    return this;
  },

  _onError: function(errorMessage) {
    console.error('Failed to load content from ' + this.options.url, errorMessage);
    this.fire('error', { error: new Error(errorMessage) });
    return this;
  },

  _coordsToLatLng: function(coords) {
    var point = L.point(coords[0], coords[1]);
    return this.options.crs.unproject(point);
  }
});

L.wfsGeoJSON = function (options) {
  return new L.WFSGeoJSON(options);
};
