import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {
  MdToolbarModule,
  MdTabsModule,
  MdDialogModule,
  MdCardModule,
  MdIconModule,
  MdButtonModule,
  MdButtonToggleModule,
  MdInputModule,
  MdSelectModule,
  MdCheckboxModule, MdAutocompleteModule
} from '@angular/material';
import 'materialize-css';
import 'angular2-materialize';
import {MaterializeModule} from 'angular2-materialize';

import {AutoCompletionDirective} from './auto-completion/auto-completion.directive';
import {AutoCompletionListComponent} from './auto-completion/auto-completion-list.component';
import {FieldErrorComponent} from './field-error.component';
import {TranslationPipe} from '../../pipe/translation.pipe';
import {SortByDirective} from './sort/sort-by.directive';
import {FileDropDirective} from './file-drop/file-drop.directive';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';
import {CommaSeparatedPipe} from '../../pipe/comma-separated.pipe';
import {FileSelectDirective} from '../application/attachment/file-select.directive';
import {InputBoxComponent} from './input-box/input-box.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
  imports: [
    MdToolbarModule,
    MdButtonModule,
    CommonModule,
    MaterializeModule,
    BrowserAnimationsModule
  ],
  declarations: [
    AutoCompletionDirective,
    AutoCompletionListComponent,
    SortByDirective,
    FieldErrorComponent,
    TranslationPipe,
    FileSelectDirective,
    FileDropDirective,
    ConfirmDialogComponent,
    CommaSeparatedPipe,
    InputBoxComponent
  ],
  exports: [
    CommonModule,
    MdToolbarModule,
    MdTabsModule,
    MdDialogModule,
    MdCardModule,
    MdIconModule,
    MdButtonModule,
    MdButtonToggleModule,
    MdInputModule,
    MdSelectModule,
    MdCheckboxModule,
    MdAutocompleteModule,
    MaterializeModule,
    AutoCompletionDirective,
    AutoCompletionListComponent,
    SortByDirective,
    FieldErrorComponent,
    TranslationPipe,
    FileSelectDirective,
    FileDropDirective,
    ConfirmDialogComponent,
    CommaSeparatedPipe,
    InputBoxComponent
  ],
  entryComponents: [
    AutoCompletionListComponent,
    ConfirmDialogComponent
  ]
})
export class AlluCommonModule {}
