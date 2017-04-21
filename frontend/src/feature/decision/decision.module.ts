import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../common/allu-common.module';
import {DecisionComponent} from './decision.component';
import {DecisionActionsComponent} from './decision-actions.component';
import {DecisionModalComponent} from './decision-modal.component';
import {ApplicationBasicInfoComponent} from '../decision/application.basic-info.component';
import {DecisionHub} from '../../service/decision/decision-hub';
import {DecisionService} from '../../service/decision/decision.service';
import {ProgressBarModule} from '../application/progressbar/progressbar.module';
import {DistributionModule} from '../application/distribution/distribution.module';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    ProgressBarModule,
    DistributionModule
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
  ],
  entryComponents: [
    DecisionModalComponent
  ]
})
export class DecisionModule {}
