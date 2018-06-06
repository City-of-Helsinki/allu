import {NgModule} from '@angular/core';
import {CommonModule, registerLocaleData} from '@angular/common';
import localeFi from '@angular/common/locales/fi';
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
  MatListModule, MatMenuModule,
  MatNativeDateModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatSelectModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule,
  MatBadgeModule, MatSlideToggleModule,
} from '@angular/material';
import {FlexLayoutModule} from '@angular/flex-layout';
import {MaterializeModule} from 'angular2-materialize';
import {FieldErrorComponent} from './field-error/field-error.component';
import {TranslationPipe} from '../../pipe/translation.pipe';
import {FileDropDirective} from './file-drop/file-drop.directive';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';
import {CommaSeparatedPipe} from '../../pipe/comma-separated.pipe';
import {FileSelectDirective} from '../application/attachment/file-select.directive';
import {InputBoxComponent, InputBoxInputDirective} from './input-box/input-box.component';
import {InputWarningDirective} from './validation/input-warning.directive';
import {AvailableToDirective} from '../../service/authorization/available-to.directive';
import {BottomBarComponent} from './bottom-bar/bottom-bar.component';
import {AlluCardComponent} from './card/allu-card.component';
import {NotificationService} from '../../service/notification/notification.service';

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
    InputBoxComponent,
    InputBoxInputDirective,
    InputWarningDirective,
    AvailableToDirective,
    BottomBarComponent,
    AlluCardComponent
  ],
  providers: [
    NotificationService
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
    MatProgressSpinnerModule,
    MatMenuModule,
    MatBadgeModule,
    MatSlideToggleModule,
    FlexLayoutModule,
    MaterializeModule,
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
    AlluCardComponent
  ],
  entryComponents: [
    ConfirmDialogComponent
  ]
})
export class AlluCommonModule {}

registerLocaleData(localeFi);
