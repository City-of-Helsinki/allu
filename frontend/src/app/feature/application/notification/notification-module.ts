import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {ApplicationNotificationComponent} from '@feature/application/notification/application-notification.component';
import {ApplicationNotificationService} from '@feature/application/notification/application-notification.service';

@NgModule({
  imports: [
    AlluCommonModule
  ],
  declarations: [
    ApplicationNotificationComponent
  ],
  providers: [
    ApplicationNotificationService
  ],
  exports: [
    ApplicationNotificationComponent
  ]
})
export class NotificationModule {}
