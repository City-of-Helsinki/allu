import {NgModule} from '@angular/core';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {RouterModule} from '@angular/router';
import {InformationRequestSummariesComponent} from '@feature/information-request/summary/information-request-summaries.component';
import {InformationRequestSummaryListComponent} from '@feature/information-request/summary/information-request-summary-list.component';
import {InformationRequestSummaryPairComponent} from '@feature/information-request/summary/information-request-summary-pair.component';
import {InformationRequestSummaryComponent} from '@feature/information-request/summary/information-request-summary.component';
import {
  InformationRequestResponseSummaryComponent
} from '@feature/information-request/summary/information-request-response-summary.component';
import {FormsModule} from '@angular/forms';
import {InformationRequestStatusComponent} from '@feature/information-request/summary/information-request-status.component';

@NgModule({
  imports: [
    AlluCommonModule,
    RouterModule.forChild([]),
    FormsModule
  ],
  declarations: [
    InformationRequestSummariesComponent,
    InformationRequestSummaryListComponent,
    InformationRequestSummaryPairComponent,
    InformationRequestSummaryComponent,
    InformationRequestResponseSummaryComponent,
    InformationRequestStatusComponent
  ],
  providers: [
  ],
  exports: [
    InformationRequestSummariesComponent
  ]
})
export class InformationRequestSummaryModule {}
