// For vendors for example jQuery, Lodash, angular2-jwt just import them here unless you plan on
// chunking vendors files for async loading. You would need to import the async loaded vendors
// at the entry point of the async loaded file. Also see custom-typings.d.ts as you also need to
// run `typings install x` where `x` is your module

// Angular 2
import '@angular/platform-browser';
import '@angular/core';
import '@angular/common';
import '@angular/http';
import '@angular/router-deprecated';

// RxJS
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/mergeMap';

// Angular 2 Material 2
// TODO(gdi2290): uncomment when material is fixed
import '@angular2-material/core';
require('../node_modules/@angular2-material/core/style/core.css');
// import '@angular2-material/sidenav';
// import '@angular2-material/toolbar';
import '@angular2-material/button';
require('../node_modules/@angular2-material/button/button.css');
// import '@angular2-material/checkbox';
// import '@angular2-material/radio';
// import '@angular2-material/progress-circle';
import '@angular2-material/card';
require('../node_modules/@angular2-material/card/card.css');
// look in platform/directives and platform/providers

// Leaflet CSS
require('../node_modules/leaflet/dist/leaflet.css');

// Leaflet.Draw CSS
require('../node_modules/leaflet-draw/dist/leaflet.draw.css');

if ('production' === ENV) {
  // Production


} else {
  // Development

}
