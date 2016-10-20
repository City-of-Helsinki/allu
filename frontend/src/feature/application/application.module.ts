import {NgModule, ModuleWithProviders} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MdCardModule, MdToolbarModule, MdProgressBarModule} from '@angular/material';
import {AlluCommonModule} from '../common/allu-common.module';

import {ApplicationComponent} from '../application/application.component.ts';
import {TypeComponent} from '../application/type/type.component';
import {OutdoorEventComponent} from '../application/outdoor-event/outdoor-event.component';
import {PromotionEventComponent} from '../application/promotion-event/promotion-event.component';
import {FileSelectDirective} from '../application/attachment/file-select.directive';
import {ApplicationAttachmentComponent} from '../application/attachment/application-attachment.component';
import {FILE_UPLOAD_DIRECTIVES, FileUploader} from 'ng2-file-upload';

import {LoadingComponent} from '../loading/loading.component';
import {MapModule} from '../map/map.module';
import {LocationState} from '../../service/application/location-state';
import {ProgressBarModule} from '../progressbar/progressbar.module';
import {applicationRoutes} from './application.routing';
import {ApplicationResolve} from './application-resolve';
import {ApplicationAttachmentHub} from './attachment/application-attachment-hub';
import {AttachmentService} from '../../service/attachment-service';


@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(applicationRoutes),
    FormsModule,
    MdCardModule,
    MdToolbarModule,
    MdProgressBarModule,
    MapModule,
    ProgressBarModule
  ],
  declarations: [
    ApplicationComponent,
    TypeComponent,
    OutdoorEventComponent,
    PromotionEventComponent,
    ApplicationAttachmentComponent,
    FILE_UPLOAD_DIRECTIVES,
    FileSelectDirective,
    LoadingComponent
  ],
  providers: [
    ApplicationAttachmentHub,
    ApplicationResolve,
    AttachmentService
  ]
})
export class ApplicationModule {}
