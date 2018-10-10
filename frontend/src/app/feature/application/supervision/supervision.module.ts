import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {SupervisionComponent} from './supervision.component';
import {SupervisionTaskComponent} from './supervision-task.component';
import {SupervisionTaskService} from '@service/supervision/supervision-task.service';
import {SupervisionApprovalModalComponent} from './supervision-approval-modal.component';
import {EffectsModule} from '@ngrx/effects';
import {SupervisionTaskEffects} from '@feature/application/supervision/effects/supervision-task-effects';
import {StoreModule} from '@ngrx/store';
import {reducers} from './reducers';
import {RouterModule} from '@angular/router';
import {
  ExcavationSupervisionApprovalModalComponent
} from '@feature/application/supervision/excavation-supervision-approval-modal.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    RouterModule,
    StoreModule.forFeature('supervisionTasks', reducers),
    EffectsModule.forFeature([
      SupervisionTaskEffects
    ])
  ],
  declarations: [
    SupervisionComponent,
    SupervisionTaskComponent,
    SupervisionApprovalModalComponent,
    ExcavationSupervisionApprovalModalComponent
  ],
  providers: [
    SupervisionTaskService
  ],
  entryComponents: [
    SupervisionApprovalModalComponent,
    ExcavationSupervisionApprovalModalComponent
  ]
})
export class SupervisionModule {}
