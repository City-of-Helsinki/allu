import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MdCardModule, MdToolbarModule, MdProgressBarModule, MdDialogModule, MdSlideToggleModule} from '@angular/material';

import {AlluCommonModule} from '../common/allu-common.module';
import {MapModule} from '../map/map.module';
import {ProgressBarModule} from './progressbar/progressbar.module';
import {LocationModule} from './location/location.module';
import {TypeModule} from './type/type.module';
import {SidebarModule} from '../sidebar/sidebar.module';
import {ApplicationComponent} from './info/application.component';
import {EventComponent} from './info/event/event.component';
import {LoadingComponent} from '../loading/loading.component';
import {applicationRoutes} from './application.routing';
import {ApplicationResolve} from './application-resolve';
import {ApplicantComponent} from './info/applicant/applicant.component';
import {ContactComponent} from './info/contact/contact.component';
import {EventDetailsComponent} from './info/event/details/event-details.component';
import {LocationDetailsComponent} from './info/location/location-details.component';
import {ShortTermRentalComponent} from './info/short-term-rental/short-term-rental.component';
import {ExcavationAnnouncementComponent} from './info/excavation-announcement/excavation-announcement.component';
import {ApplicationActionsComponent} from './info/application-actions.component';
import {CableReportComponent} from './info/cable-report/cable-report.component';
import {CableInfoComponent} from './info/cable-report/cable-info.component';
import {DefaultTextModalComponent} from './default-text/default-text-modal.component';
import {NoteComponent} from './info/note/note.component';
import {ApplicationInfoComponent} from './info/application-info.component';
import {CommentModule} from './comment/comment.module';
import {TagBarModule} from './tagbar/tagbar.module';
import {AttachmentModule} from './attachment/attachment.module';
import {TrafficArrangementComponent} from './info/traffic-arrangement/traffic-arrangement.component';
import {PlacementContractComponent} from './info/placement-contract/placement-contract.component';
import {ApplicationHistoryModule} from './history/application-history.module';
import {TermsModule} from './terms/terms.module';


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
    MdSlideToggleModule,
    MapModule,
    ProgressBarModule,
    LocationModule,
    TypeModule,
    SidebarModule,
    CommentModule,
    TagBarModule,
    AttachmentModule,
    ApplicationHistoryModule,
    TermsModule
  ],
  declarations: [
    ApplicationComponent,
    ApplicationInfoComponent,
    EventComponent,
    ShortTermRentalComponent,
    CableReportComponent,
    CableInfoComponent,
    ExcavationAnnouncementComponent,
    ApplicationActionsComponent,
    LoadingComponent,
    ApplicantComponent,
    ContactComponent,
    EventDetailsComponent,
    LocationDetailsComponent,
    DefaultTextModalComponent,
    NoteComponent,
    TrafficArrangementComponent,
    PlacementContractComponent
  ],
  providers: [
    ApplicationResolve
  ],
  entryComponents: [
    DefaultTextModalComponent
  ]
})
export class ApplicationModule {}
