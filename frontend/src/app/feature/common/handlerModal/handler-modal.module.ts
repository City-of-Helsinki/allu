import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HandlerModalComponent} from './handler-modal.component';
import {CommonModule} from '@angular/common';
import {AlluCommonModule} from '../allu-common.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    AlluCommonModule
  ],
  declarations: [
    HandlerModalComponent
  ],
  exports: [
    HandlerModalComponent
  ],
  entryComponents: [
    HandlerModalComponent
  ]
})
export class HandlerModalModule {}
