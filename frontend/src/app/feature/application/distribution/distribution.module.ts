import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatRadioModule} from '@angular/material/radio';
import {AlluCommonModule} from '../../common/allu-common.module';
import {DistributionComponent} from './distribution.component';
import {DistributionListComponent} from './distribution-list/distribution-list.component';
import {DistributionListEvents} from './distribution-list/distribution-list-events';
import {DistributionSelectionComponent} from '@feature/application/distribution/distribution-list/distribution-selection.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    MatRadioModule
  ],
  declarations: [
    DistributionComponent,
    DistributionListComponent,
    DistributionSelectionComponent
  ],
  providers: [
    DistributionListEvents
  ],
  exports: [
    DistributionComponent,
    DistributionListComponent,
    DistributionSelectionComponent
  ]
})
export class DistributionModule {}
