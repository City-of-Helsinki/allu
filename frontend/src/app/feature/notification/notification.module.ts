import {NgModule} from '@angular/core';
import {StoreModule} from '@ngrx/store';
import {reducers} from './reducers';
import {NotificationService} from '@feature/notification/notification.service';

@NgModule({
  imports: [
    StoreModule.forFeature('notification', reducers)
  ],
  providers: [
    NotificationService
  ]
})
export class NotificationModule {}
