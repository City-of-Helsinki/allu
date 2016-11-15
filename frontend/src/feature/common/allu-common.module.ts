import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MdToolbarModule} from '@angular/material';
import {MaterializeDirective} from 'angular2-materialize';
import {AutoCompletionDirective} from './auto-completion/auto-completion.directive.ts';
import {AutoCompletionListComponent} from './auto-completion/auto-completion-list.component.ts';

@NgModule({
  imports: [
    MdToolbarModule,
    CommonModule
  ],
  declarations: [
    MaterializeDirective,
    AutoCompletionDirective,
    AutoCompletionListComponent
  ],
  exports: [
    CommonModule,
    MdToolbarModule,
    MaterializeDirective,
    AutoCompletionDirective,
    AutoCompletionListComponent
  ],
  entryComponents: [
    AutoCompletionListComponent
  ]
})
export class AlluCommonModule {}
