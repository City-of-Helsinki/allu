import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {
  ApplicationNotificationComponent,
  ApplicationNotificationEntryDirective
} from '@feature/application/notification/application-notification.component';
import {ExternalUpdateNotificationService} from '@feature/application/notification/external-update/external-update-notification.service';
import {
  ExternalUpdateNotificationComponent
} from '@feature/application/notification/external-update/external-update-notification.component';
import {OwnerNotificationComponent} from '@feature/application/notification/owner-notification/owner-notification.component';
import {RouterModule} from '@angular/router';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild([])
  ],
  declarations: [
    ApplicationNotificationComponent,
    ApplicationNotificationEntryDirective,
    ExternalUpdateNotificationComponent,
    OwnerNotificationComponent
  ],
  providers: [
    ExternalUpdateNotificationService
  ],
  exports: [
    ApplicationNotificationComponent,
    ApplicationNotificationEntryDirective,
    ExternalUpdateNotificationComponent,
    OwnerNotificationComponent
  ]
})
export class NotificationModule {}
