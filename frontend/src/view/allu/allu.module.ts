import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {APP_BASE_HREF} from '@angular/common';
import {Http, HttpModule} from '@angular/http';
import {AuthHttp, AuthConfig} from 'angular2-jwt/angular2-jwt';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MdButtonModule} from '@angular2-material/button';
import {MdCardModule} from '@angular2-material/card';
import {MdCheckboxModule} from '@angular2-material/checkbox';
import {MdCoreModule} from '@angular2-material/core';
import {MdInputModule} from '@angular2-material/input';
import {MdRadioModule} from '@angular2-material/radio';
import {MdToolbarModule} from '@angular2-material/toolbar';
import {MaterializeDirective} from 'angular2-materialize';
import {FILE_UPLOAD_DIRECTIVES, FileUploader} from 'ng2-file-upload';
import {MapUtil} from '../../service/map.util.ts';
import {EventService} from '../../event/event.service';
import {TaskManagerService} from '../../service/task/task-manager.service';
import {ApplicationService} from '../../service/application/application.service.ts';
import {GeolocationService} from '../../service/geolocation.service';
import {SearchService} from '../../service/search.service';
import {MapHub} from '../../service/map-hub';
import {ApplicationHub} from '../../service/application/application-hub';
import {UIStateHub} from '../../service/ui-state/ui-state-hub';
import {AttachmentService} from '../../service/attachment-service';
import {LocationState} from '../../service/application/location-state';
import {DecisionHub} from '../../service/decision/decision-hub';
import {DecisionService} from '../../service/decision/decision.service';
import {AuthGuard} from '../../component/login/auth-guard.service';
import {AlluComponent}   from './allu.component';
import {MapSearchComponent} from '../mapsearch/mapsearch.component';
import {ApplicationComponent} from '../application/application.component';
import {TypeComponent} from '../../component/application/type/type.component';
import {OutdoorEventComponent} from '../../component/application/outdoor-event/outdoor-event.component';
import {PromotionEventComponent} from '../../component/application/promotion-event/promotion-event.component';
import {WorkQueueComponent} from '../../component/workqueue/workqueue.component';
import {LocationComponent} from '../../component/location/location.component';
import {SummaryComponent} from '../../component/application/summary/summary.component';
import {DecisionComponent} from '../../component/application/decision/decision.component';
import {SearchComponent} from '../../component/search/search.component';
import {Login} from '../../component/login/login.component';
import {rootRoutes} from '../../view/allu/allu.routing';
import {FileSelectDirective} from '../../component/application/attachment/file-select.directive';
import {DecisionModalComponent} from '../../component/application/decision/decision-modal.component';
import {ProgressbarComponent} from '../../component/progressbar/progressbar.component';
import {ApplicationBasicInfoComponent} from '../../component/application/decision/application.basic-info.component';
import {DecisionActionsComponent} from '../../component/application/decision/decision-actions.component';
import {MapComponent} from '../../component/map/map.component';
import {ApplicationListComponent} from '../../component/application/list/application-list.component';
import {ToolbarComponent} from '../../component/toolbar/toolbar.component';
import {NavbarComponent} from '../../component/navbar/navbar.component';
import {ApplicationAttachmentComponent} from '../../component/application/attachment/application-attachment.component';
import {LoadingComponent} from '../../component/loading/loading.component';
import {SearchbarComponent} from '../../component/searchbar/searchbar.component';

@NgModule({
  declarations: [
    AlluComponent,
    ProgressbarComponent,
    ToolbarComponent,
    NavbarComponent,
    MapSearchComponent,
    ApplicationComponent,
    ApplicationListComponent,
    TypeComponent,
    OutdoorEventComponent,
    PromotionEventComponent,
    WorkQueueComponent,
    LocationComponent,
    SummaryComponent,
    DecisionComponent,
    SearchComponent,
    SearchbarComponent,
    MapComponent,
    Login,
    MaterializeDirective,
    FILE_UPLOAD_DIRECTIVES,
    FileSelectDirective,
    DecisionModalComponent,
    DecisionActionsComponent,
    ApplicationBasicInfoComponent,
    LoadingComponent,
    ApplicationAttachmentComponent
  ],
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    RouterModule.forRoot(rootRoutes),
    // Material
    MdCoreModule.forRoot(),
    MdButtonModule,
    MdCardModule,
    MdCheckboxModule,
    MdInputModule,
    MdRadioModule,
    MdToolbarModule
  ],
  bootstrap: [AlluComponent],
  providers: [
    EventService,
    TaskManagerService,
    ApplicationService,
    MapUtil,
    SearchService,
    MapHub,
    GeolocationService,
    ApplicationHub,
    LocationState,
    UIStateHub,
    AttachmentService,
    DecisionService,
    DecisionHub,
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
