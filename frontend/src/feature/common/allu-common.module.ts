import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {
  MdAutocompleteModule,
  MdButtonModule,
  MdButtonToggleModule,
  MdCardModule,
  MdCheckboxModule,
  MdDatepickerModule,
  MdDialogModule,
  MdIconModule,
  MdInputModule,
  MdNativeDateModule,
  MdRadioModule,
  MdSelectModule,
  MdTabsModule,
  MdToolbarModule,
  MdProgressBarModule,
  MdTooltipModule,
  MdListModule
} from '@angular/material';
import {FlexLayoutModule} from '@angular/flex-layout';
import 'materialize-css';
import 'angular2-materialize';
import {MaterializeModule} from 'angular2-materialize';

import {AutoCompletionDirective} from './auto-completion/auto-completion.directive';
import {AutoCompletionListComponent} from './auto-completion/auto-completion-list.component';
import {FieldErrorComponent} from './field-error/field-error.component';
import {TranslationPipe} from '../../pipe/translation.pipe';
import {SortByDirective} from './sort/sort-by.directive';
import {FileDropDirective} from './file-drop/file-drop.directive';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';
import {CommaSeparatedPipe} from '../../pipe/comma-separated.pipe';
import {FileSelectDirective} from '../application/attachment/file-select.directive';
import {InputBoxComponent, InputBoxInputDirective} from './input-box/input-box.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {InputWarningDirective} from './validation/input-warning.directive';
import {AvailableToDirective} from '../../service/authorization/available-to.directive';

@NgModule({
  imports: [
    MdToolbarModule,
    MdButtonModule,
    MdRadioModule,
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
    InputBoxComponent,
    InputBoxInputDirective,
    InputWarningDirective,
    AvailableToDirective
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
    MdRadioModule,
    MdAutocompleteModule,
    MdDatepickerModule,
    MdNativeDateModule,
    MdProgressBarModule,
    MdTooltipModule,
    MdListModule,
    FlexLayoutModule,
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
    InputBoxComponent,
    InputBoxInputDirective,
    InputWarningDirective,
    AvailableToDirective
  ],
  entryComponents: [
    AutoCompletionListComponent,
    ConfirmDialogComponent
  ]
})
export class AlluCommonModule {}
