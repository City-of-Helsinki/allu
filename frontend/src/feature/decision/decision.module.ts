import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MdCardModule} from '@angular/material';
import {AlluCommonModule} from '../common/allu-common.module';
import {DecisionComponent} from './decision.component';
import {DecisionActionsComponent} from './decision-actions.component';
import {DecisionModalComponent} from './decision-modal.component';
import {ApplicationBasicInfoComponent} from '../decision/application.basic-info.component';
import {DecisionHub} from '../../service/decision/decision-hub';
import {DecisionService} from '../../service/decision/decision.service';
import {ProgressBarModule} from '../progressbar/progressbar.module';

@NgModule({
  imports: [
    FormsModule,
    AlluCommonModule,
    MdCardModule,
    ProgressBarModule
  ],
  declarations: [
    DecisionComponent,
    DecisionActionsComponent,
    DecisionModalComponent,
    ApplicationBasicInfoComponent
  ],
  providers: [
    DecisionHub,
    DecisionService
  ]
})
export class DecisionModule {}
