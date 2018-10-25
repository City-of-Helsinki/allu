import {NgModule} from '@angular/core';
import {CommonModule, registerLocaleData} from '@angular/common';
import localeFi from '@angular/common/locales/fi';
import {
  MatAutocompleteModule,
  MatBadgeModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatCheckboxModule,
  MatDatepickerModule,
  MatDialogModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatSelectModule,
  MatSlideToggleModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule,
} from '@angular/material';
import {FlexLayoutModule} from '@angular/flex-layout';
import {FieldErrorComponent} from './field-error/field-error.component';
import {TranslationPipe} from '@app/pipe/translation.pipe';
import {FileDropDirective} from './file-drop/file-drop.directive';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';
import {CommaSeparatedPipe} from '@app/pipe/comma-separated.pipe';
import {FileSelectDirective} from '../application/attachment/file-select.directive';
import {InputBoxComponent, InputBoxInputDirective} from './input-box/input-box.component';
import {InputWarningDirective} from './validation/input-warning.directive';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {BottomBarComponent} from './bottom-bar/bottom-bar.component';
import {AlluCardComponent} from './card/allu-card.component';
import {KeysPipe} from '@app/pipe/keys-pipe';
import {CentsToEurosPipe} from '@app/pipe/cents-to-euros.pipe';

@NgModule({
  imports: [
    CommonModule,
    MatButtonModule,
    MatDialogModule,
    MatCardModule
  ],
  declarations: [
    FieldErrorComponent,
    TranslationPipe,
    FileSelectDirective,
    FileDropDirective,
    ConfirmDialogComponent,
    CommaSeparatedPipe,
    KeysPipe,
    CentsToEurosPipe,
    InputBoxComponent,
    InputBoxInputDirective,
    InputWarningDirective,
    AvailableToDirective,
    BottomBarComponent,
    AlluCardComponent
  ],
  providers: [],
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
    MatProgressSpinnerModule,
    MatMenuModule,
    MatBadgeModule,
    MatSlideToggleModule,
    FlexLayoutModule,
    FieldErrorComponent,
    TranslationPipe,
    FileSelectDirective,
    FileDropDirective,
    ConfirmDialogComponent,
    CommaSeparatedPipe,
    InputBoxComponent,
    InputBoxInputDirective,
    InputWarningDirective,
    AvailableToDirective,
    BottomBarComponent,
    AlluCardComponent,
    KeysPipe,
    CentsToEurosPipe
  ],
  entryComponents: [
    ConfirmDialogComponent
  ]
})
export class AlluCommonModule {}

registerLocaleData(localeFi);
