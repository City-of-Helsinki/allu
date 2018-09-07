import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatRadioModule} from '@angular/material';
import {AlluCommonModule} from '../../common/allu-common.module';
import {DistributionComponent} from './distribution.component';
import {DistributionListComponent} from './distribution-list/distribution-list.component';
import {DistributionListEvents} from './distribution-list/distribution-list-events';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    MatRadioModule
  ],
  declarations: [
    DistributionComponent,
    DistributionListComponent
  ],
  providers: [
    DistributionListEvents
  ],
  exports: [
    DistributionComponent,
    DistributionListComponent
  ]
})
export class DistributionModule {}
