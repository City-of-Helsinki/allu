import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MdToolbarModule} from '@angular/material';
import {MaterializeDirective} from 'angular2-materialize';

@NgModule({
  imports: [
    MdToolbarModule,
    CommonModule
  ],
  declarations: [
    MaterializeDirective
  ],
  exports: [
    CommonModule,
    MdToolbarModule,
    MaterializeDirective
  ]
})
export class AlluCommonModule {}
