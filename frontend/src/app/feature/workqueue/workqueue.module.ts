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
import {WorkQueueService} from './workqueue-search/workqueue.service';
import {WorkQueueHub} from './workqueue-search/workqueue-hub';
import {SelectionGroupModule} from '../common/selection-group/selection-group.module';
import {CommentsModalComponent} from '../application/comment/comments-modal.component';
import {OwnerModalModule} from '../common/ownerModal/owner-modal.module';
import {ApplicationWorkItemStore} from './application-work-item-store';
import {ApplicationWorkItemDatasource} from './content/application-work-item-datasource';

@NgModule({
  imports: [
    ReactiveFormsModule,
    FormsModule,
    AlluCommonModule,
    MatButtonToggleModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatChipsModule,
    SelectionGroupModule,
    OwnerModalModule
  ],
  declarations: [
    WorkQueueComponent,
    WorkQueueFilterComponent,
    WorkQueueContentComponent,
    CommentsModalComponent
  ],
  providers: [
    WorkQueueHub,
    ApplicationWorkItemStore,
    WorkQueueService
  ],
  entryComponents: [
    CommentsModalComponent
  ]
})
export class WorkQueueModule {}
