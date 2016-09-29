import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MdToolbarModule} from '@angular2-material/toolbar';
import {MaterializeDirective} from 'angular2-materialize';

@NgModule({
  imports: [
    MdToolbarModule
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
