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

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    StoreModule.forFeature('supervisionTasks', reducers),
    EffectsModule.forFeature([
      SupervisionTaskEffects
    ])
  ],
  declarations: [
    SupervisionComponent,
    SupervisionTaskComponent,
    SupervisionApprovalModalComponent
  ],
  providers: [
    SupervisionTaskService
  ],
  entryComponents: [
    SupervisionApprovalModalComponent
  ]
})
export class SupervisionModule {}
