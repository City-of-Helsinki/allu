import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {SupervisionComponent} from './supervision.component';
import {SupervisionTaskComponent} from './supervision-task.component';
import {SupervisionTaskService} from '../../../service/supervision/supervision-task.service';
import {SupervisionTaskStore} from '../../../service/supervision/supervision-task-store';
import {SupervisionApprovalModalComponent} from './supervision-approval-modal.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule
  ],
  declarations: [
    SupervisionComponent,
    SupervisionTaskComponent,
    SupervisionApprovalModalComponent
  ],
  providers: [
    SupervisionTaskService,
    SupervisionTaskStore
  ],
  entryComponents: [
    SupervisionApprovalModalComponent
  ]
})
export class SupervisionModule {}
