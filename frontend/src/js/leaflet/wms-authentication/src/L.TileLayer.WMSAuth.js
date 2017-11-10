'use strict';

const request = require('superagent');

L.TileLayer.WMSAuth = L.TileLayer.WMS.extend({
  token: undefined,
  timeoutOptions: {
    response: 10000,  // Wait 10 seconds for the server to start sending,
    deadline: 60000, // but allow 1 minute for the file to finish loading.
  },

  initialize: function (url, options) {
    this.token = options.token;
    this.timeoutOptions = Object.assign(this.timeoutOptions, options.timeout);
    delete options.timeout;
    delete options.token; // need to delete property so it is not added as url parameters by parent class
    L.TileLayer.WMS.prototype.initialize.call(this, url, options);
  },
  // @method createTile(coords: Object, done?: Function): HTMLElement
  // Called only internally, overrides GridLayer's [`createTile()`](#gridlayer-createtile)
  // to return an `<img>` HTML element with the appropiate image URL given `coords`. The `done`
  // callback is called when the tile has been loaded.
  createTile: function (coords, done) {
    var tile = document.createElement('img');
    var url = this.getTileUrl(coords);

    /*
     Alt tag is set to empty string to keep screen readers from reading URL and for compliance reasons
     http://www.w3.org/TR/WCAG20-TECHS/H67
    */
    tile.alt = '';

    /*
     Set role="presentation" to force screen readers to ignore this
     https://www.w3.org/TR/wai-aria/roles#textalternativecomputation
    */
    tile.setAttribute('role', 'presentation');

    if (this.options.crossOrigin) {
      tile.crossOrigin = '';
    }

    const self = this;
    request
      .get(url)
      .timeout(this.timeoutOptions)
      .set('Authorization', 'Bearer ' + this.token)
      .responseType('blob')
      .then(
        function(res) {
          tile.src = URL.createObjectURL(res.body);
          L.TileLayer.WMS.prototype._tileOnLoad.call(self, done, tile);
        },
        function(err) {
          if (err.timeout) {
            console.error('Fetching map tile took too long');
          }
          L.TileLayer.WMS.prototype._tileOnError.call(self, done, tile, err);
        }
      );
    return tile;
  },
});

// @factory L.tileLayer.wmsAuth(baseUrl: String, options: TileLayer.WMS options)
// Instantiates a WMS tile layer with authentication object given a base URL of the WMS service and a WMS parameters/options object.
// options object should contain jwt token which is used for authenticating
L.tileLayer.wmsAuth = function (url, options) {
  return new L.TileLayer.WMSAuth(url, options);
};
