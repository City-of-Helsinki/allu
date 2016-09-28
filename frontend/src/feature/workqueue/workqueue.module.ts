import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {WorkqueueService} from '../../service/workqueue.service';
import {WorkQueueComponent} from './workqueue.component';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    FormsModule,
    AlluCommonModule
  ],
  declarations: [
    WorkQueueComponent
  ],
  providers: [
    WorkqueueService
  ]
})
export class WorkQueueModule {}
