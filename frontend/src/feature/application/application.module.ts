import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MdCardModule, MdToolbarModule, MdProgressBarModule, MdDialogModule} from '@angular/material';
import {FILE_UPLOAD_DIRECTIVES} from 'ng2-file-upload';

import {AlluCommonModule} from '../common/allu-common.module';
import {MapModule} from '../map/map.module';
import {ProgressBarModule} from '../progressbar/progressbar.module';
import {LocationModule} from './location/location.module';
import {TypeModule} from './type/type.module';
import {SidebarModule} from '../sidebar/sidebar.module';

import {FileSelectDirective} from './info/attachment/file-select.directive';

import {ApplicationComponent} from './info/application.component.ts';
import {EventComponent} from './info/event/event.component';
import {ApplicationAttachmentComponent} from './info/attachment/application-attachment.component';
import {LoadingComponent} from '../loading/loading.component';
import {applicationRoutes} from './application.routing';
import {ApplicationResolve} from './application-resolve';
import {ApplicationAttachmentHub} from './info/attachment/application-attachment-hub';
import {ApplicantComponent} from './info/applicant/applicant.component';
import {ContactComponent} from './info/contact/contact.component';
import {EventDetailsComponent} from './info/event/details/event-details.component.ts';
import {LocationDetailsComponent} from './info/location/location-details.component';
import {ShortTermRentalComponent} from './info/short-term-rental/short-term-rental.component.ts';
import {ExcavationAnnouncementComponent} from './info/excavation-announcement/excavation-announcement.component';

import {AttachmentService} from '../../service/attachment-service';
import {ApplicationActionsComponent} from './info/application-actions.component';
import {CableReportComponent} from './info/cable-report/cable-report.component';
import {CableInfoComponent} from './info/cable-report/cable-info.component.ts';
import {DefaultTextModalComponent} from './default-text/default-text-modal.component';
import {ApplicationState} from '../../service/application/application-state';
import {NoteComponent} from './info/note/note.component';
import {ApplicationInfoComponent} from './info/application-info.component';


@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild(applicationRoutes),
    FormsModule,
    ReactiveFormsModule,
    MdCardModule,
    MdToolbarModule,
    MdProgressBarModule,
    MdDialogModule,
    MapModule,
    ProgressBarModule,
    LocationModule,
    TypeModule,
    SidebarModule
  ],
  declarations: [
    ApplicationComponent,
    ApplicationInfoComponent,
    EventComponent,
    ShortTermRentalComponent,
    CableReportComponent,
    CableInfoComponent,
    ExcavationAnnouncementComponent,
    ApplicationAttachmentComponent,
    ApplicationActionsComponent,
    FILE_UPLOAD_DIRECTIVES,
    FileSelectDirective,
    LoadingComponent,
    ApplicantComponent,
    ContactComponent,
    EventDetailsComponent,
    LocationDetailsComponent,
    DefaultTextModalComponent,
    NoteComponent
  ],
  providers: [
    ApplicationAttachmentHub,
    ApplicationResolve,
    AttachmentService,
    ApplicationState
  ],
  entryComponents: [
    DefaultTextModalComponent
  ]
})
export class ApplicationModule {}
