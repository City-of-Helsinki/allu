import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdRadioModule} from '@angular/material';
import {AlluCommonModule} from '../../common/allu-common.module';
import {CommunicationComponent} from './communication.component';
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
    CommunicationComponent
  ],
  providers: [
  ],
  exports: [
    CommunicationComponent
  ]
})
export class CommunicationModule {}
