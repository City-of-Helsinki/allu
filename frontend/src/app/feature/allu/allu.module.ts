import {LOCALE_ID, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {APP_BASE_HREF} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {ActionReducer, StoreModule} from '@ngrx/store';
import {EffectsModule} from '@ngrx/effects';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {environment} from 'environments/environment';

import {ApplicationModule} from '../application/application.module';
import {ApplicationService} from '../../service/application/application.service';
import {LocationService} from '../../service/location.service';
import {MapStore} from '../../service/map/map-store';
import {AuthGuard} from '../../service/authorization/auth-guard.service';
import {AdminGuard} from '@app/service/authorization/admin-guard.service';
import {AlluComponent} from './allu.component';
import {rootRoutes} from './allu.routing';
import {MapSearchModule} from '../mapsearch/mapsearch.module';
import {WorkQueueModule} from '../workqueue/workqueue.module';
import {LocationModule} from '../application/location/location.module';
import {DecisionModule} from '../decision/decision.module';
import {SearchModule} from '../search/search.module';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {LoginModule} from '../login/login.module';
import {AdminModule} from '../admin/admin.module';
import {ProjectModule} from '../project/project.module';
import {SidebarModule} from '../sidebar/sidebar.module';
import {ApplicationStore} from '../../service/application/application-store';
import {Oauth2Component} from '../oauth2/oauth2.component';
import {ErrorHandler} from '../../service/error/error-handler.service';
import {DefaultTextService} from '../../service/application/default-text.service';
import {LocationState} from '../../service/application/location-state';
import {CustomerService} from '../../service/customer/customer.service';
import {CustomerRegistryModule} from '../customerregistry/customer-registry.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {DateAdapter} from '@angular/material/core';
import {MatLegacyPaginatorIntl as MatPaginatorIntl} from '@angular/material/legacy-paginator';
import {AlluDateAdapter} from '../../util/allu-date-adapter';
import {CurrentUser} from '../../service/user/current-user';
import {ConfigService} from '../../service/config/config.service';
import {SupervisionWorkqueueModule} from '../supervision-workqueue/supervision-workqueue.module';
import {UserService} from '../../service/user/user-service';
import {DownloadModule} from '../download/download.module';
import {CanDeactivateGuard} from '../../service/common/can-deactivate-guard';
import {CanActivateLogin} from '../../service/authorization/can-activate-login';
import {AlluPaginatorIntl} from '../../service/common/allu-paginator-intl';
import {CodeSetService} from '../../service/codeset/codeset.service';
import {CityDistrictEffects} from './effects/city-district-effects';
import {reducers} from './reducers';
import {AuthModule} from '../auth/auth.module';
import {CustomIconRegistry} from '../../service/common/custom-icon-registry';
import {HttpClientModule} from '@angular/common/http';
import {httpInterceptorProviders} from '../../http-interceptors';
import {MetadataService} from '../../service/meta/metadata.service';
import {ToastrModule} from 'ngx-toastr';
import {CodeSetEffects} from './effects/code-set-effects';
import {NotificationModule} from '@feature/notification/notification.module';
import {ConfigurationService} from '@service/config/configuration.service';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {UserEffects} from '@feature/allu/effects/user-effects';
import {ContactService} from '@service/customer/contact.service';
import {FixedLocationEffects} from '@feature/allu/effects/fixed-location-effects';
import {BulkApprovalModule} from '@feature/decision/bulk/bulk-approval.module';

/*
todo uprade replace deprecated logger
export function logger(reducer: ActionReducer<any>): any {
  return storeLogger({
    collapsed: true
  })(reducer);
}

export const metaReducers = environment.production ? [] : [logger];
*/

@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(rootRoutes, {}),
    BrowserAnimationsModule,
    StoreModule.forRoot(reducers, {
      runtimeChecks: { // Disable checks to avoid Leaflet causing errors
        strictStateImmutability: false,
        strictActionImmutability: false,
      },
    }),
    EffectsModule.forRoot([
      CityDistrictEffects,
      CodeSetEffects,
      UserEffects,
      FixedLocationEffects
    ]),
    StoreDevtoolsModule.instrument({
      maxAge: 25, // Retains last 25 states
      logOnly: environment.production // Restrict extension to log-only mode
    , connectInZone: true}),
    ToastrModule.forRoot(),
    // App modules
    ApplicationModule,
    ProjectModule,
    LoginModule,
    AuthModule,
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
    DownloadModule,
    NotificationModule,
    BulkApprovalModule
  ],
  declarations: [
    AlluComponent,
    Oauth2Component
  ],
  bootstrap: [AlluComponent],
  providers: [
    httpInterceptorProviders,
    ApplicationService,
    UserService,
    LocationService,
    CustomerService,
    ContactService,
    MapStore,
    AuthGuard,
    AdminGuard,
    CanDeactivateGuard,
    CanActivateLogin,
    ApplicationStore,
    LocationState,
    ErrorHandler,
    DefaultTextService,
    CurrentUser,
    ConfigService,
    CodeSetService,
    ConfigurationService,
    ConfigurationHelperService,
    CustomIconRegistry,
    MetadataService,
    { provide: APP_BASE_HREF,  useValue: '/' },
    { provide: DateAdapter, useClass: AlluDateAdapter },
    { provide: LOCALE_ID, useValue: 'fi-FI' },
    { provide: MatPaginatorIntl, useClass: AlluPaginatorIntl }
  ]
})
export class AlluModule {}
