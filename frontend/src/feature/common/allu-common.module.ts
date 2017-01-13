import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MdToolbarModule, MdTabsModule, MdDialogModule, MdCardModule} from '@angular/material';
import {MaterializeDirective} from 'angular2-materialize';
import {AutoCompletionDirective} from './auto-completion/auto-completion.directive.ts';
import {AutoCompletionListComponent} from './auto-completion/auto-completion-list.component.ts';
import {FieldErrorComponent} from './field-error.component';
import {TranslationPipe} from '../../pipe/translation.pipe';
import {SortByDirective} from './sort/sort-by.directive';
import {FileDropDirective} from './file-drop/file-drop.directive';

@NgModule({
  imports: [
    MdToolbarModule,
    CommonModule
  ],
  declarations: [
    MaterializeDirective,
    AutoCompletionDirective,
    AutoCompletionListComponent,
    SortByDirective,
    FieldErrorComponent,
    TranslationPipe,
    FileDropDirective
  ],
  exports: [
    CommonModule,
    MdToolbarModule,
    MdTabsModule,
    MdDialogModule,
    MdCardModule,
    MaterializeDirective,
    AutoCompletionDirective,
    AutoCompletionListComponent,
    SortByDirective,
    FieldErrorComponent,
    TranslationPipe,
    FileDropDirective
  ],
  entryComponents: [
    AutoCompletionListComponent
  ]
})
export class AlluCommonModule {}
