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
import {
  AreaRentalSupervisionApprovalModalComponent
} from '@feature/application/supervision/area-rental-supervision-approval-modal.component';
import {MapModule} from '@feature/map/map.module';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    RouterModule,
    StoreModule.forFeature('supervisionTasks', reducers),
    EffectsModule.forFeature([
      SupervisionTaskEffects
    ]),
    MapModule
  ],
  declarations: [
    SupervisionComponent,
    SupervisionTaskComponent,
    SupervisionApprovalModalComponent,
    ExcavationSupervisionApprovalModalComponent,
    AreaRentalSupervisionApprovalModalComponent
  ],
  providers: [
    SupervisionTaskService
  ],
  entryComponents: [
    SupervisionApprovalModalComponent,
    ExcavationSupervisionApprovalModalComponent,
    AreaRentalSupervisionApprovalModalComponent
  ]
})
export class SupervisionModule {}
