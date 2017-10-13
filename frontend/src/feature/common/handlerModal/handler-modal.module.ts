import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HandlerModalComponent} from './handler-modal.component';
import {MatButtonModule, MatOptionModule, MatSelectModule} from '@angular/material';
import {CommonModule} from '@angular/common';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    MatSelectModule,
    MatOptionModule,
    MatButtonModule
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
