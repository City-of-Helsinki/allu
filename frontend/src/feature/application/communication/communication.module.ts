import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdRadioModule} from '@angular/material';
import {AlluCommonModule} from '../../common/allu-common.module';
import {CommunicationComponent} from './communication.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    MdRadioModule
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
