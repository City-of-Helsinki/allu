import {NgModule} from '@angular/core';
import {ReactiveFormsModule, FormsModule} from '@angular/forms';

import {WorkQueueComponent} from './workqueue.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {WorkQueueFilterComponent} from './filter/workqueue-filter.component';
import {WorkQueueContentComponent} from './content/workqueue-content.component';
import {HandlerModalComponent} from './handlerModal/handler-modal.component';
import {WorkQueueService} from './workqueue-search/workqueue.service';
import {WorkQueueHub} from './workqueue-search/workqueue-hub';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    AlluCommonModule
  ],
  declarations: [
    WorkQueueComponent,
    WorkQueueFilterComponent,
    WorkQueueContentComponent,
    HandlerModalComponent
  ],
  providers: [
    WorkQueueHub,
    WorkQueueService
  ]
})
export class WorkQueueModule {}
