import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AlluCommonModule} from '../../common/allu-common.module';
import {DateReportingModalComponent} from '@feature/application/date-reporting/date-reporting-modal.component';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        AlluCommonModule
    ],
    declarations: [
        DateReportingModalComponent
    ],
    exports: [
        DateReportingModalComponent
    ]
})
export class DateReportingModule {}
