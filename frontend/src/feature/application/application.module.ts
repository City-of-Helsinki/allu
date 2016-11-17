import {NgModule, ModuleWithProviders} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MdCardModule, MdToolbarModule, MdProgressBarModule} from '@angular/material';
import {FILE_UPLOAD_DIRECTIVES, FileUploader} from 'ng2-file-upload';

import {AlluCommonModule} from '../common/allu-common.module';
import {MapModule} from '../map/map.module';
import {ProgressBarModule} from '../progressbar/progressbar.module';
import {LocationModule} from './location/location.module';
import {TypeModule} from './type/type.module';

import {FileSelectDirective} from './info/attachment/file-select.directive';

import {ApplicationComponent} from './info/application.component.ts';
import {OutdoorEventComponent} from './info/outdoor-event/outdoor-event.component';
import {PromotionEventComponent} from './info/promotion-event/promotion-event.component';
import {ApplicationAttachmentComponent} from './info/attachment/application-attachment.component';
import {LoadingComponent} from '../loading/loading.component';
import {applicationRoutes} from './application.routing';
import {ApplicationResolve} from './application-resolve';
import {ApplicationAttachmentHub} from './info/attachment/application-attachment-hub';
import {ApplicantComponent} from './info/applicant/applicant.component';
import {ContactComponent} from './info/contact/contact.component';
import {EventDetailsComponent} from './info/outdoor-event/details/event-details.component.ts';
import {LocationDetailsComponent} from './info/location/location-details.component';
import {ShortTermRentalComponent} from './info/short-term-rental/short-term-rental.component.ts';

import {LocationState} from '../../service/application/location-state';
import {AttachmentService} from '../../service/attachment-service';
import {ApplicationActionsComponent} from './info/application-actions.component';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(applicationRoutes),
    FormsModule,
    ReactiveFormsModule,
    MdCardModule,
    MdToolbarModule,
    MdProgressBarModule,
    MapModule,
    ProgressBarModule,
    LocationModule,
    TypeModule
  ],
  declarations: [
    ApplicationComponent,
    OutdoorEventComponent,
    PromotionEventComponent,
    ShortTermRentalComponent,
    ApplicationAttachmentComponent,
    ApplicationActionsComponent,
    FILE_UPLOAD_DIRECTIVES,
    FileSelectDirective,
    LoadingComponent,
    ApplicantComponent,
    ContactComponent,
    EventDetailsComponent,
    LocationDetailsComponent
  ],
  providers: [
    ApplicationAttachmentHub,
    ApplicationResolve,
    AttachmentService
  ]
})
export class ApplicationModule {}
