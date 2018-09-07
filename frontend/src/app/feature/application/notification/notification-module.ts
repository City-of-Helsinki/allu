import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {ApplicationNotificationComponent} from '@feature/application/notification/application-notification.component';

@NgModule({
  imports: [
    AlluCommonModule
  ],
  declarations: [
    ApplicationNotificationComponent
  ],
  exports: [
    ApplicationNotificationComponent
  ]
})
export class NotificationModule {}
