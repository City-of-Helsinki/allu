import {provide, enableProdMode} from 'angular2/core';
// import {APP_BASE_HREF} from 'angular2/router';
import {bootstrap} from 'angular2/platform/browser';
import {ROUTER_PROVIDERS, APP_BASE_HREF} from 'angular2/router';
// import {AppComponent} from './app/components/app.component';
import {AlluComponent} from './view/allu/allu.component';
import {EventService} from './shared/services/event/event.service';
import {TaskManager} from './shared/services/task/task-manager.service';
import {NameListService} from './shared/services/name-list.service';
import {MapService} from './service/map.service';
import {GeocodingService} from './service/geocoding.service';
import {HTTP_PROVIDERS} from 'angular2/http';

if ('<%= ENV %>' === 'prod') { enableProdMode(); }

bootstrap(AlluComponent, [
  ROUTER_PROVIDERS,
  HTTP_PROVIDERS,
  EventService,
  TaskManager,
  NameListService,
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
