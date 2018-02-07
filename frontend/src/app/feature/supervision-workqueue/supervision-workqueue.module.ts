import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {
  MatButtonToggleModule, MatChipsModule, MatPaginatorModule, MatSortModule,
  MatTableModule
} from '@angular/material';
import {AlluCommonModule} from '../common/allu-common.module';
import {WorkQueueComponent} from './workqueue.component';
import {WorkQueueFilterComponent} from './filter/workqueue-filter.component';
import {WorkQueueContentComponent} from './content/workqueue-content.component';
import {SupervisionTaskService} from '../../service/supervision/supervision-task.service';
import {SupervisionWorkItemStore} from './supervision-work-item-store';
import {OwnerModalModule} from '../common/ownerModal/owner-modal.module';
import {RouterModule} from '@angular/router';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    AlluCommonModule,
    MatButtonToggleModule,
    MatChipsModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    OwnerModalModule
  ],
  declarations: [
    WorkQueueComponent,
    WorkQueueFilterComponent,
    WorkQueueContentComponent
  ],
  providers: [
    SupervisionTaskService,
    SupervisionWorkItemStore
  ]
})
export class SupervisionWorkqueueModule {}
