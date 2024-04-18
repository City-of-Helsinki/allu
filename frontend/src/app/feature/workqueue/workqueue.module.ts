import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatChipsModule} from '@angular/material/chips';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';

import {WorkQueueComponent} from './workqueue.component';
import {AlluCommonModule} from '../common/allu-common.module';
import {WorkQueueFilterComponent} from './filter/workqueue-filter.component';
import {WorkQueueContentComponent} from './content/workqueue-content.component';
import {SelectionGroupModule} from '../common/selection-group/selection-group.module';
import {CommentsModalComponent} from '../comment/comments-modal.component';
import {OwnerModalModule} from '../common/ownerModal/owner-modal.module';
import {RouterModule} from '@angular/router';
import {StoredFilterModule} from '../stored-filter/stored-filter.module';
import {StoreModule} from '@ngrx/store';
import {reducersProvider, reducersToken} from '@feature/workqueue/reducers';
import {CommentModule} from '@feature/comment/comment.module';

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
        StoredFilterModule,
        StoreModule.forFeature('workQueue', reducersToken),
        CommentModule
    ],
    declarations: [
        WorkQueueComponent,
        WorkQueueFilterComponent,
        WorkQueueContentComponent,
        CommentsModalComponent
    ],
    providers: [
        reducersProvider
    ]
})
export class WorkQueueModule {}
