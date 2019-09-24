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

@NgModule({
  imports: [
    AlluCommonModule
  ],
  declarations: [
    ApplicationNotificationComponent,
    ApplicationNotificationEntryDirective,
    ExternalUpdateNotificationComponent
  ],
  providers: [
    ExternalUpdateNotificationService
  ],
  exports: [
    ApplicationNotificationComponent,
    ApplicationNotificationEntryDirective,
    ExternalUpdateNotificationComponent
  ]
})
export class NotificationModule {}
