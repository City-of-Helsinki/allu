import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MdSlideToggleModule} from '@angular/material';

import {AlluCommonModule} from '../../common/allu-common.module';
import {CommunicationComponent} from './communication.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AlluCommonModule,
    MdSlideToggleModule
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
