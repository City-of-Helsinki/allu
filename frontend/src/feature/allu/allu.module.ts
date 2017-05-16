import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {APP_BASE_HREF} from '@angular/common';
import {Http, HttpModule} from '@angular/http';
import {AuthConfig, AuthHttp} from 'angular2-jwt/angular2-jwt';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';

import {ApplicationModule} from '../application/application.module';
import {ApplicationService} from '../../service/application/application.service';
import {LocationService} from '../../service/location.service';
import {MapHub} from '../../service/map/map-hub';
import {ApplicationHub} from '../../service/application/application-hub';
import {UIStateHub} from '../../service/ui-state/ui-state-hub';
import {AuthGuard} from '../../feature/login/auth-guard.service';
import {AlluComponent} from './allu.component';
import {rootRoutes} from './allu.routing';
import {MapSearchModule} from '../mapsearch/mapsearch.module';
import {WorkQueueModule} from '../workqueue/workqueue.module';
import {LocationModule} from '../application/location/location.module';
import {DecisionModule} from '../decision/decision.module';
import {SearchModule} from '../search/search.module';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {LoginModule} from '../login/login.module';
import {UserHub} from '../../service/user/user-hub';
import {UserService} from '../../service/user/user-service';
import {AdminModule} from '../admin/admin.module';
import {ProjectModule} from '../project/project.module';
import {SidebarModule} from '../sidebar/sidebar.module';
import {ApplicationState} from '../../service/application/application-state';
import {Oauth2Component} from '../oauth2/oauth2.component';
import {ErrorHandler} from '../../service/error/error-handler.service';
import {DefaultTextService} from '../../service/application/default-text.service';
import {LocationState} from '../../service/application/location-state';
import {CustomerHub} from '../../service/customer/customer-hub';
import {CustomerService} from '../../service/customer/customer.service';
import {CustomerRegistryModule} from '../customerregistry/customer-registry.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {DateAdapter} from '@angular/material';
import {AlluDateAdapter} from '../../util/allu-date-adapter';


@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    RouterModule.forRoot(rootRoutes),
    BrowserAnimationsModule,
    // App modules
    ApplicationModule,
    ProjectModule,
    LoginModule,
    ToolbarModule,
    MapSearchModule,
    LocationModule.forRoot(),
    WorkQueueModule,
    DecisionModule,
    SearchModule,
    AdminModule,
    SidebarModule,
    CustomerRegistryModule
  ],
  declarations: [
    AlluComponent,
    Oauth2Component
  ],
  bootstrap: [AlluComponent],
  providers: [
    ApplicationService,
    ApplicationHub,
    UserService,
    LocationService,
    CustomerService,
    CustomerHub,
    UserHub,
    MapHub,
    UIStateHub,
    AuthGuard,
    ApplicationState,
    LocationState,
    ErrorHandler,
    DefaultTextService,
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
    },
    { provide: DateAdapter, useClass: AlluDateAdapter }
  ]
})
export class AlluModule {}
