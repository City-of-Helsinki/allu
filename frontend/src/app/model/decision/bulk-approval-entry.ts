import {ApplicationStatus} from '@model/application/application-status';
import {DistributionEntry} from '@model/common/distribution-entry';
import {OperationStatus} from '@model/common/operation-status';
import {ErrorInfo} from '@service/error/error-info';

export class BulkApprovalEntry {
  constructor(
    public id?: number,
    public applicationId?: string,
    public targetState?: ApplicationStatus,
    public bulkApprovalBlocked?: boolean,
    public bulkApprovalBlockedReason?: string,
    public distributionList: DistributionEntry[] = []) {}
}

export interface EntryStatus {
  id: number;
  status: OperationStatus;
  error?: ErrorInfo;
}
