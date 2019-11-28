import {Routes} from '@angular/router';
import {BulkApprovalModalContainerComponent} from './bulk-approval-modal-container.component';
import {AuthGuard} from '@app/service/authorization/auth-guard.service';

export const bulkApprovalRoutes: Routes = [
  { path: 'bulkApproval', component: BulkApprovalModalContainerComponent, canActivate: [AuthGuard] }
];
