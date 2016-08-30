import {provide, enableProdMode} from '@angular/core';
import {APP_BASE_HREF} from '@angular/common';
import {bootstrap} from '@angular/platform-browser-dynamic';
import {ROUTER_PROVIDERS} from '@angular/router-deprecated';
import {Http, HTTP_PROVIDERS} from '@angular/http';
import {AlluComponent} from './view/allu/allu.component';
import {MapUtil} from './service/map.util.ts';
import {GeocodingService} from './service/geocoding.service';
import {EventService} from './event/event.service';
import {AuthHttp, AuthConfig} from 'angular2-jwt/angular2-jwt';
import {TaskManagerService} from './service/task/task-manager.service';
import {ApplicationService} from './service/application.service';
import {GeolocationService} from './service/geolocation.service';
import {SearchService} from './service/search.service';
import {MapHub} from './service/map-hub';
import {ApplicationHub} from './service/application-hub';

if ('<%= ENV %>' === 'prod') { enableProdMode(); }

bootstrap(AlluComponent, [
  ROUTER_PROVIDERS,
  HTTP_PROVIDERS,
  EventService,
  TaskManagerService,
  ApplicationService,
  MapUtil,
  SearchService,
  GeocodingService,
  MapHub,
  GeolocationService,
  ApplicationHub,
  provide(
    APP_BASE_HREF, { useValue: '/' }),
  provide(
    AuthHttp, {
      useFactory: (http) => {
        return new AuthHttp(new AuthConfig({
          headerName: 'Authorization',
          headerPrefix: 'Bearer',
          tokenName: 'jwt',
          tokenGetter: (() => localStorage.getItem('jwt')),
          globalHeaders: [{'Content-Type': 'application/json'}],
          noJwtError: true,
          noTokenScheme: false
        }), http);
      },
      deps: [Http]
    })
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
