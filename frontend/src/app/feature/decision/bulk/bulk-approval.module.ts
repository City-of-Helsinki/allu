import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AlluCommonModule} from '@app/feature/common/allu-common.module';
import {BulkApprovalModalContainerComponent} from './bulk-approval-modal-container.component';
import {BulkApprovalModalComponent} from './bulk-approval-modal.component';

@NgModule({
  imports: [
    RouterModule,
    AlluCommonModule
  ],
  declarations: [
    BulkApprovalModalContainerComponent,
    BulkApprovalModalComponent
  ],
  providers: [
  ],
  entryComponents: [
    BulkApprovalModalComponent
  ],
  exports: [
    BulkApprovalModalContainerComponent
  ]
})
export class BulkApprovalModule {}
