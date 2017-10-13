import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {
  MatAutocompleteModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatCheckboxModule,
  MatDatepickerModule,
  MatDialogModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatNativeDateModule,
  MatProgressBarModule,
  MatRadioModule,
  MatSelectModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule
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
    MatToolbarModule,
    MatButtonModule,
    MatRadioModule,
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
    MatToolbarModule,
    MatTabsModule,
    MatDialogModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatRadioModule,
    MatAutocompleteModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressBarModule,
    MatTooltipModule,
    MatListModule,
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
