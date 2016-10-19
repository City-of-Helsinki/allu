import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {APP_BASE_HREF} from '@angular/common';
import {Http, HttpModule} from '@angular/http';
import {AuthHttp, AuthConfig} from 'angular2-jwt/angular2-jwt';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MaterialModule} from '@angular/material';

import {ApplicationModule} from '../application/application.module';

import {MapUtil} from '../../service/map.util.ts';
import {ApplicationService} from '../../service/application/application.service.ts';
import {GeolocationService} from '../../service/geolocation.service';
import {MapHub} from '../../service/map-hub';
import {ApplicationHub} from '../../service/application/application-hub';
import {UIStateHub} from '../../service/ui-state/ui-state-hub';
import {LocationState} from '../../service/application/location-state';
import {AuthGuard} from '../../feature/login/auth-guard.service';

import {AlluComponent}   from './allu.component';
import {Login} from '../login/login.component';
import {ToolbarComponent} from '../toolbar/toolbar.component';
import {NavbarComponent} from '../toolbar/navbar/navbar.component';
import {rootRoutes} from './allu.routing';
import {MapSearchModule} from '../mapsearch/mapsearch.module';
import {WorkQueueModule} from '../workqueue/workqueue.module';
import {LocationModule} from '../location/location.module';
import {AlluCommonModule} from '../common/allu-common.module';
import {DecisionModule} from '../decision/decision.module';
import {SearchModule} from '../search/search.module';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {LoginModule} from '../login/login.module';
import {UserHub} from '../../service/user/user-hub';
import {UserService} from '../../service/user/user-service';


@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    RouterModule.forRoot(rootRoutes),
    // Material
    MaterialModule.forRoot(),
    // App modules
    ApplicationModule,
    LoginModule,
    ToolbarModule,
    MapSearchModule,
    LocationModule.forRoot(),
    WorkQueueModule,
    DecisionModule,
    SearchModule
  ],
  declarations: [
    AlluComponent
  ],
  bootstrap: [AlluComponent],
  providers: [
    ApplicationService,
    UserService,
    GeolocationService,
    ApplicationHub,
    UserHub,
    MapHub,
    UIStateHub,
    AuthGuard,
    { provide: APP_BASE_HREF,  useValue: '/' },
    { provide: AuthHttp, useFactory: (http) => {
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
    }
  ]
})
export class AlluModule {}
