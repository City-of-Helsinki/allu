import {NgModule} from '@angular/core';
import {AlluCommonModule} from '../../common/allu-common.module';
import {DistributionListComponent} from './distribution-list.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@NgModule({
  imports: [
    AlluCommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [
    DistributionListComponent
  ],
  exports: [
    DistributionListComponent
  ]
})
export class MailingListModule {}
