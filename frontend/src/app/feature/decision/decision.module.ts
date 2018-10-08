import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {DecisionComponent} from './decision.component';
import {DecisionActionsComponent} from './decision-actions.component';
import {DecisionModalComponent} from './decision-modal.component';
import {ApplicationBasicInfoComponent} from '../decision/application.basic-info.component';
import {DecisionService} from '@service/decision/decision.service';
import {ContractService} from '@service/contract/contract.service';
import {ProgressBarModule} from '@feature/application/progressbar/progressbar.module';
import {DistributionModule} from '@feature/application/distribution/distribution.module';
import {DecisionProposalModalComponent} from './proposal/decision-proposal-modal.component';
import {DecisionProposalComponent} from './proposal/decision-proposal.component';
import {AttachmentThumbnailsComponent} from './attachment-thumbnails.component';
import {PdfModule} from '@feature/pdf/pdf.module';
import {StoreModule} from '@ngrx/store';
import {reducersProvider, reducersToken} from './reducers';
import {EffectsModule} from '@ngrx/effects';
import {DecisionEffects} from '@feature/decision/effects/decision-effects';
import {DecisionDocumentComponent} from '@feature/decision/documents/decision-document.component';
import {RouterModule} from '@angular/router';
import {DecisionDocumentsComponent} from '@feature/decision/documents/decision-documents.component';
import {ContractEffects} from '@feature/decision/effects/contract-effects';
import {DecisionTabResolve} from '@feature/decision/decision-tab-resolve';
import {ContractGuard} from '@feature/decision/documents/contract-guard';
import {ContractActionsComponent} from '@feature/decision/contract-actions.component';
import {ContractApprovalModalComponent} from '@feature/decision/contract/contract-approval-modal.component';
import {ApprovalDocumentService} from '@service/decision/approval-document.service';
import {ApprovalDocumentEffects} from '@feature/decision/effects/approval-document-effects';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    StoreModule.forFeature('decision', reducersToken),
    EffectsModule.forFeature([
      DecisionEffects,
      ContractEffects,
      ApprovalDocumentEffects
    ]),
    AlluCommonModule,
    ProgressBarModule,
    DistributionModule,
    PdfModule
  ],
  declarations: [
    DecisionComponent,
    DecisionDocumentsComponent,
    DecisionDocumentComponent,
    DecisionActionsComponent,
    ContractActionsComponent,
    DecisionModalComponent,
    ApplicationBasicInfoComponent,
    DecisionProposalModalComponent,
    DecisionProposalComponent,
    AttachmentThumbnailsComponent,
    ContractApprovalModalComponent
  ],
  providers: [
    DecisionService,
    ContractService,
    ApprovalDocumentService,
    DecisionTabResolve,
    ContractGuard,
    reducersProvider
  ],
  entryComponents: [
    DecisionModalComponent,
    DecisionProposalModalComponent,
    ContractApprovalModalComponent
  ],
  exports: [
    DecisionComponent
  ]
})
export class DecisionModule {}
