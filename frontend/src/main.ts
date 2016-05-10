import {provide, enableProdMode} from 'angular2/core';
// import {APP_BASE_HREF} from 'angular2/router';
import {bootstrap} from 'angular2/platform/browser';
import {ROUTER_PROVIDERS, APP_BASE_HREF} from 'angular2/router';
import {HTTP_PROVIDERS} from 'angular2/http';
// import {AppComponent} from './app/components/app.component';
import {AlluComponent} from './view/allu/allu.component';
import {MapService} from './service/map.service';
import {GeocodingService} from './service/geocoding.service';
import {EventService} from './event/event.service';
import {TaskManager} from './service/task/task-manager.service';

if ('<%= ENV %>' === 'prod') { enableProdMode(); }

bootstrap(AlluComponent, [
  ROUTER_PROVIDERS,
  HTTP_PROVIDERS,
  EventService,
  TaskManager,
  MapService,
  GeocodingService,
  provide(APP_BASE_HREF, { useValue: '/' })
]);

// In order to start the Service Worker located at "./sw.js"
// uncomment this line. More about Service Workers here
// https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API/Using_Service_Workers
// if ('serviceWorker' in navigator) {
//   (<any>navigator).serviceWorker.register('./sw.js').then(function(registration) {
//     console.log('ServiceWorker registration successful with scope: ',    registration.scope);
//   }).catch(function(err) {
//     console.log('ServiceWorker registration failed: ', err);
//   });
// }
