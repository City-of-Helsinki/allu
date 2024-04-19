import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatLegacyChipsModule as MatChipsModule} from '@angular/material/legacy-chips';
import {MatLegacyPaginatorModule as MatPaginatorModule} from '@angular/material/legacy-paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatLegacyTableModule as MatTableModule} from '@angular/material/legacy-table';
import {AlluCommonModule} from '../common/allu-common.module';
import {WorkQueueComponent} from './workqueue.component';
import {WorkQueueFilterComponent} from './filter/workqueue-filter.component';
import {WorkQueueContentComponent} from './content/workqueue-content.component';
import {SupervisionTaskService} from '../../service/supervision/supervision-task.service';
import {OwnerModalModule} from '../common/ownerModal/owner-modal.module';
import {RouterModule} from '@angular/router';
import {StoredFilterModule} from '../stored-filter/stored-filter.module';
import {StoreModule} from '@ngrx/store';
import {reducersProvider, reducersToken} from './reducers';
import {EffectsModule} from '@ngrx/effects';
import {SupervisionTaskSearchEffects} from '@feature/application/supervision/effects/supervision-task-search-effects';

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
    OwnerModalModule,
    StoredFilterModule,
    StoreModule.forFeature('supervisionWorkQueue', reducersToken),
    EffectsModule.forFeature([
      SupervisionTaskSearchEffects
    ]),
  ],
  declarations: [
    WorkQueueComponent,
    WorkQueueFilterComponent,
    WorkQueueContentComponent
  ],
  providers: [
    SupervisionTaskService,
    reducersProvider
  ]
})
export class SupervisionWorkqueueModule {}
