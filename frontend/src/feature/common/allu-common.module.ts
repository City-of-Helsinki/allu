import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MdToolbarModule, MdTabsModule, MdDialogModule} from '@angular/material';
import {MaterializeDirective} from 'angular2-materialize';
import {AutoCompletionDirective} from './auto-completion/auto-completion.directive.ts';
import {AutoCompletionListComponent} from './auto-completion/auto-completion-list.component.ts';
import {FieldErrorComponent} from './field-error.component';

@NgModule({
  imports: [
    MdToolbarModule,
    CommonModule
  ],
  declarations: [
    MaterializeDirective,
    AutoCompletionDirective,
    AutoCompletionListComponent,
    FieldErrorComponent
  ],
  exports: [
    CommonModule,
    MdToolbarModule,
    MdTabsModule,
    MdDialogModule,
    MaterializeDirective,
    AutoCompletionDirective,
    AutoCompletionListComponent,
    FieldErrorComponent
  ],
  entryComponents: [
    AutoCompletionListComponent
  ]
})
export class AlluCommonModule {}
