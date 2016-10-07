import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {WorkQueueComponent} from './workqueue.component';
import {AlluCommonModule} from '../common/allu-common.module';

@NgModule({
  imports: [
    FormsModule,
    AlluCommonModule
  ],
  declarations: [
    WorkQueueComponent
  ]
})
export class WorkQueueModule {}
