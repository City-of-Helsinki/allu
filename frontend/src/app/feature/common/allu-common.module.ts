import {NgModule} from '@angular/core';
import {CommonModule, registerLocaleData} from '@angular/common';
import localeFi from '@angular/common/locales/fi';
import {MatLegacyAutocompleteModule as MatAutocompleteModule} from '@angular/material/legacy-autocomplete';
import {MatBadgeModule} from '@angular/material/badge';
import {MatLegacyButtonModule as MatButtonModule} from '@angular/material/legacy-button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
import {MatLegacyCheckboxModule as MatCheckboxModule} from '@angular/material/legacy-checkbox';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatLegacyDialogModule as MatDialogModule} from '@angular/material/legacy-dialog';
import {MatIconModule} from '@angular/material/icon';
import {MatLegacyInputModule as MatInputModule} from '@angular/material/legacy-input';
import {MatLegacyListModule as MatListModule} from '@angular/material/legacy-list';
import {MatLegacyMenuModule as MatMenuModule} from '@angular/material/legacy-menu';
import {MatLegacyProgressBarModule as MatProgressBarModule} from '@angular/material/legacy-progress-bar';
import {MatLegacyProgressSpinnerModule as MatProgressSpinnerModule} from '@angular/material/legacy-progress-spinner';
import {MatLegacyRadioModule as MatRadioModule} from '@angular/material/legacy-radio';
import {MatLegacySelectModule as MatSelectModule} from '@angular/material/legacy-select';
import {MatLegacySlideToggleModule as MatSlideToggleModule} from '@angular/material/legacy-slide-toggle';
import {MatLegacyTabsModule as MatTabsModule} from '@angular/material/legacy-tabs';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatLegacyTooltipModule as MatTooltipModule} from '@angular/material/legacy-tooltip';
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
import {CountryNamePipe} from '@app/pipe/country-name-pipe';
import {PartialListDisplayComponent} from '@feature/common/partial-list-display/partial-list-display.component';
import {ValidityTimeComponent} from '@feature/common/validity-time/validity-time.component';
import {ValidityEndTimeComponent} from '@feature/common/validity-time/validity-end-time.component';
import {ValidityStartTimeComponent} from '@feature/common/validity-time/validity-start-time.component';
import {LocalLoaderService} from '@feature/common/local-loader/local-loader.service';
import {ErrorPageComponent} from '@feature/common/error-page/error-page.component';
import {FileSizePipe} from '@app/pipe/file-size.pipe';
import { LoadingIndicatorComponent } from './loading-indicator/loading-indicator.component';

@NgModule({
    imports: [
        CommonModule,
        MatButtonModule,
        MatDialogModule,
        MatCardModule,
        MatProgressSpinnerModule
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
        CountryNamePipe,
        InputBoxComponent,
        InputBoxInputDirective,
        InputWarningDirective,
        AvailableToDirective,
        BottomBarComponent,
        AlluCardComponent,
        PartialListDisplayComponent,
        ValidityTimeComponent,
        ValidityStartTimeComponent,
        ValidityEndTimeComponent,
        ErrorPageComponent,
        FileSizePipe,
        LoadingIndicatorComponent
    ],
    providers: [
        LocalLoaderService
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
        CentsToEurosPipe,
        CountryNamePipe,
        PartialListDisplayComponent,
        ValidityTimeComponent,
        ValidityStartTimeComponent,
        ValidityEndTimeComponent,
        ErrorPageComponent,
        FileSizePipe,
        LoadingIndicatorComponent
    ]
})
export class AlluCommonModule {}

registerLocaleData(localeFi);
