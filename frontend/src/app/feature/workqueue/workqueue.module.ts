import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {
  MatButtonToggleModule, MatChipsModule, MatPaginatorModule, MatSortModule,
  MatTableModule
} from '@angular/material';

import {WorkQueueComponent} from './workqueue.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {WorkQueueFilterComponent} from './filter/workqueue-filter.component';
import {WorkQueueContentComponent} from './content/workqueue-content.component';
import {SelectionGroupModule} from '../common/selection-group/selection-group.module';
import {CommentsModalComponent} from '../application/comment/comments-modal.component';
import {OwnerModalModule} from '../common/ownerModal/owner-modal.module';
import {ApplicationWorkItemStore} from './application-work-item-store';
import {RouterModule} from '@angular/router';
import {StoredFilterModule} from '../stored-filter/stored-filter.module';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    AlluCommonModule,
    MatButtonToggleModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatChipsModule,
    SelectionGroupModule,
    OwnerModalModule,
    StoredFilterModule
  ],
  declarations: [
    WorkQueueComponent,
    WorkQueueFilterComponent,
    WorkQueueContentComponent,
    CommentsModalComponent
  ],
  providers: [
    ApplicationWorkItemStore
  ],
  entryComponents: [
    CommentsModalComponent
  ]
})
export class WorkQueueModule {}
