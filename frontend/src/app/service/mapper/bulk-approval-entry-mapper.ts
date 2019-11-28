import {ApplicationStatus} from '@app/model/application/application-status';
import {BulkApprovalEntry} from '@app/model/decision/bulk-approval-entry';
import {BackendDistributionEntry} from '../backend-model/backend-distribution-entry';
import {DistributionMapper} from './distribution-mapper';

export interface BackendBulkApprovalEntry {
  id: number;
  applicationId: string;
  targetState: ApplicationStatus;
  bulkApprovalBlocked: boolean;
  bulkApprovalBlockedReason: string;
  distributionList: BackendDistributionEntry[];
}

export class BulkApprovalEntryMapper {
  public static mapBackendList(approvalEntries: BackendBulkApprovalEntry[] = []): BulkApprovalEntry[] {
    return approvalEntries.map(s => this.mapBackend(s));
  }

  public static mapBackend(approvalEntry: BackendBulkApprovalEntry): BulkApprovalEntry {
    return new BulkApprovalEntry(
      approvalEntry.id,
      approvalEntry.applicationId,
      approvalEntry.targetState,
      approvalEntry.bulkApprovalBlocked,
      approvalEntry.bulkApprovalBlockedReason,
      DistributionMapper.mapBackendList(approvalEntry.distributionList)
    );
  }
}
