import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdRadioModule} from '@angular/material';
import {AlluCommonModule} from '../../common/allu-common.module';
import {DistributionComponent} from './distribution.component';
import {MailingListModule} from '../distribution-list/distribution-list.module';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    MdRadioModule,
    MailingListModule
  ],
  declarations: [
    DistributionComponent
  ],
  providers: [
  ],
  exports: [
    DistributionComponent
  ]
})
export class DistributionModule {}
