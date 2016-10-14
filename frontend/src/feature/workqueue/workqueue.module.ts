import {NgModule} from '@angular/core';
import {ReactiveFormsModule, FormsModule} from '@angular/forms';
import {MdTabsModule} from '@angular2-material/tabs';

import {WorkQueueComponent} from './workqueue.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {WorkQueueFilterComponent} from './filter/workqueue-filter.component';
import {WorkQueueContentComponent} from './content/workqueue-content.component';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    AlluCommonModule,
    MdTabsModule
  ],
  declarations: [
    WorkQueueComponent,
    WorkQueueFilterComponent,
    WorkQueueContentComponent
  ]
})
export class WorkQueueModule {}
