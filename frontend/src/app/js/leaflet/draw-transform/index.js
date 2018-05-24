var L = require('leaflet');
require('leaflet-draw');
require('leaflet-path-transform');

require('../path-transform/src/Path.Transform');
require('./src/Draw.Tooltip');

require('./src/Edit.SimpleShape.Transform');
require('./src/Edit.Circle.Transform');
require('./src/Edit.Poly.Transform');
require('./src/Edit.Rectangle.Transform');

require('./src/EditToolbar.Edit');

module.exports = L.Edit.Poly;
