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
import {SupervisionTaskLocationComponent} from '@feature/application/supervision/location/supervision-task-location.component';
import { LoadingIndicatorComponent } from '@app/feature/common/loading-indicator/loading-indicator.component';

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
        AreaRentalSupervisionApprovalModalComponent,
        SupervisionTaskLocationComponent
    ],
    providers: [
        SupervisionTaskService
    ]
})
export class SupervisionModule {}
