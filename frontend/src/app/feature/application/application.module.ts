import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatToolbarModule} from '@angular/material/toolbar';

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
import {CustomerComponent} from './info/customer/customer.component';
import {ContactComponent} from './info/contact/contact.component';
import {LocationDetailsComponent} from './info/location/location-details.component';
import {ShortTermRentalComponent} from './info/short-term-rental/short-term-rental.component';
import {ExcavationAnnouncementComponent} from './info/excavation-announcement/excavation-announcement.component';
import {ApplicationActionsComponent} from './info/application-actions.component';
import {CableReportComponent} from './info/cable-report/cable-report.component';
import {CableInfoComponent} from './info/cable-report/cable-info.component';
import {DefaultTextModalComponent} from './default-text/default-text-modal.component';
import {NoteComponent} from './info/note/note.component';
import {ApplicationInfoComponent} from './info/application-info.component';
import {CommentModule} from '../comment/comment.module';
import {TagBarModule} from './tagbar/tagbar.module';
import {AttachmentModule} from './attachment/attachment.module';
import {TrafficArrangementComponent} from './info/traffic-arrangement/traffic-arrangement.component';
import {PlacementContractComponent} from './info/placement-contract/placement-contract.component';
import {DefaultTextModule} from './default-text/default-text.module';
import {AreaRentalComponent} from './info/area-rental/area-rental.component';
import {DistributionModule} from './distribution/distribution.module';
import {PricingInfoComponent} from './info/pricing-info/pricing-info.component';
import {InvoicingModule} from './invoicing/invoicing.module';
import {RecurringComponent} from './info/recurring/recurring.component';
import {CustomerRegistryModule} from '../customerregistry/customer-registry.module';
import {SupervisionModule} from './supervision/supervision.module';
import {ApplicationDraftService} from '../../service/application/application-draft.service';
import {ApplicationCommentsComponent} from './comment/application-comments.component';
import {StoreModule} from '@ngrx/store';
import {reducersProvider, reducersToken} from './reducers';
import {EffectsModule} from '@ngrx/effects';
import {ApplicationTagEffects} from './effects/application-tag-effects';
import {HistoryModule} from '../history/history.module';
import {ApplicationHistoryComponent} from './history/application-history.component';
import {ApplicationEffects} from './effects/application-effects';
import {InformationRequestModule} from '../information-request/information-request.module';
import {ApplicationInfoBaseComponent} from './info/application-info-base.component';
import {PdfModule} from '@feature/pdf/pdf.module';
import {DecisionModule} from '@feature/decision/decision.module';
import {NotificationModule} from '@feature/application/notification/notification-module';
import {ExcavationAnnouncementEffects} from '@feature/application/effects/excavation-announcement-effects';
import {ExcavationAnnouncementService} from '@service/application/excavation-announcement.service';
import {DateReportingModule} from '@feature/application/date-reporting/date-reporting.module';
import {DateReportingEffects} from '@feature/application/effects/date-reporting-effects';
import {DateReportingService} from '@service/application/date-reporting.service';
import {ApplicationReplacementHistoryEffects} from '@feature/application/effects/application-replacement-history-effects';
import {ApplicationIdentifierSelectModule} from '@feature/application/identifier-select/application-identifier-select.module';
import {ApplicationSearchEffects} from '@feature/application/effects/application-search-effects';
import {OwnerNotificationModule} from '@feature/application/owner-notification/owner-notification.module';

@NgModule({
    imports: [
        AlluCommonModule,
        RouterModule.forChild(applicationRoutes),
        StoreModule.forFeature('application', reducersToken),
        EffectsModule.forFeature([
            ApplicationEffects,
            ApplicationTagEffects,
            ExcavationAnnouncementEffects,
            DateReportingEffects,
            ApplicationReplacementHistoryEffects,
            ApplicationSearchEffects
        ]),
        FormsModule,
        ReactiveFormsModule,
        MatCardModule,
        MatToolbarModule,
        MatDialogModule,
        MatSlideToggleModule,
        MapModule,
        ProgressBarModule,
        LocationModule,
        TypeModule,
        SidebarModule,
        CommentModule,
        TagBarModule,
        AttachmentModule,
        HistoryModule,
        DefaultTextModule,
        DistributionModule,
        InvoicingModule,
        CustomerRegistryModule,
        SupervisionModule,
        InformationRequestModule,
        DecisionModule,
        PdfModule,
        NotificationModule,
        DateReportingModule,
        ApplicationIdentifierSelectModule,
        OwnerNotificationModule
    ],
    declarations: [
        ApplicationComponent,
        ApplicationInfoBaseComponent,
        ApplicationInfoComponent,
        EventComponent,
        ShortTermRentalComponent,
        CableReportComponent,
        CableInfoComponent,
        ExcavationAnnouncementComponent,
        ApplicationActionsComponent,
        LoadingComponent,
        CustomerComponent,
        ContactComponent,
        LocationDetailsComponent,
        DefaultTextModalComponent,
        NoteComponent,
        TrafficArrangementComponent,
        PlacementContractComponent,
        AreaRentalComponent,
        PricingInfoComponent,
        RecurringComponent,
        ApplicationCommentsComponent,
        ApplicationHistoryComponent
    ],
    providers: [
        ApplicationResolve,
        ApplicationDraftService,
        ExcavationAnnouncementService,
        DateReportingService,
        reducersProvider
    ]
})
export class ApplicationModule {}
