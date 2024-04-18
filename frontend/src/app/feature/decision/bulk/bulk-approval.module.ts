import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AlluCommonModule} from '@app/feature/common/allu-common.module';
import {BulkApprovalModalContainerComponent} from './bulk-approval-modal-container.component';
import {BulkApprovalModalComponent} from './bulk-approval-modal.component';
import {BulkApprovalEntryComponent} from '@feature/decision/bulk/bulk-approval-entry.component';
import {BulkApprovalEntryListComponent} from '@feature/decision/bulk/bulk-approval-entry-list.component';

@NgModule({
    imports: [
        RouterModule,
        AlluCommonModule
    ],
    declarations: [
        BulkApprovalModalContainerComponent,
        BulkApprovalModalComponent,
        BulkApprovalEntryComponent,
        BulkApprovalEntryListComponent
    ],
    providers: [],
    exports: [
        BulkApprovalModalContainerComponent
    ]
})
export class BulkApprovalModule {}
