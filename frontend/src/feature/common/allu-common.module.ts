import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {
  MdToolbarModule,
  MdTabsModule,
  MdDialogModule,
  MdCardModule,
  MdIconModule,
  MdButtonModule} from '@angular/material';
import {MaterializeDirective} from 'angular2-materialize';
import {AutoCompletionDirective} from './auto-completion/auto-completion.directive.ts';
import {AutoCompletionListComponent} from './auto-completion/auto-completion-list.component.ts';
import {FieldErrorComponent} from './field-error.component';
import {TranslationPipe} from '../../pipe/translation.pipe';
import {SortByDirective} from './sort/sort-by.directive';
import {FileDropDirective} from './file-drop/file-drop.directive';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';

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
    FileDropDirective,
    ConfirmDialogComponent
  ],
  exports: [
    CommonModule,
    MdToolbarModule,
    MdTabsModule,
    MdDialogModule,
    MdCardModule,
    MdIconModule,
    MdButtonModule,
    MaterializeDirective,
    AutoCompletionDirective,
    AutoCompletionListComponent,
    SortByDirective,
    FieldErrorComponent,
    TranslationPipe,
    FileDropDirective,
    ConfirmDialogComponent
  ],
  entryComponents: [
    AutoCompletionListComponent,
    ConfirmDialogComponent
  ]
})
export class AlluCommonModule {}
