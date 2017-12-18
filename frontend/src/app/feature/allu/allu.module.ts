import {NgModule, LOCALE_ID} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {APP_BASE_HREF} from '@angular/common';
import {Http, HttpModule} from '@angular/http';
import {AuthConfig, AuthHttp} from 'angular2-jwt/angular2-jwt';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import '../../rxjs-extensions';

import {ApplicationModule} from '../application/application.module';
import {ApplicationService} from '../../service/application/application.service';
import {LocationService} from '../../service/location.service';
import {MapHub} from '../../service/map/map-hub';
import {UIStateHub} from '../../service/ui-state/ui-state-hub';
import {AuthGuard} from '../../service/authorization/auth-guard.service';
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
import {AdminModule} from '../admin/admin.module';
import {ProjectModule} from '../project/project.module';
import {SidebarModule} from '../sidebar/sidebar.module';
import {ApplicationStore} from '../../service/application/application-store';
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
import {CurrentUser} from '../../service/user/current-user';
import {ConfigService} from '../../service/config/config.service';
import {SupervisionWorkqueueModule} from '../supervision-workqueue/supervision-workqueue.module';
import {UserService} from '../../service/user/user-service';
import {DownloadModule} from '../download/download.module';
import {CanDeactivateGuard} from '../../service/common/can-deactivate-guard';
import {CanActivateLogin} from '../../service/authorization/can-activate-login';


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
    SupervisionWorkqueueModule,
    DecisionModule,
    SearchModule,
    AdminModule,
    SidebarModule,
    CustomerRegistryModule,
    DownloadModule
  ],
  declarations: [
    AlluComponent,
    Oauth2Component
  ],
  bootstrap: [AlluComponent],
  providers: [
    ApplicationService,
    UserService,
    LocationService,
    CustomerService,
    CustomerHub,
    UserHub,
    MapHub,
    UIStateHub,
    AuthGuard,
    CanDeactivateGuard,
    CanActivateLogin,
    ApplicationStore,
    LocationState,
    ErrorHandler,
    DefaultTextService,
    CurrentUser,
    ConfigService,
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
    { provide: DateAdapter, useClass: AlluDateAdapter },
    { provide: LOCALE_ID, useValue: 'fi-FI' }
  ]
})
export class AlluModule {}
