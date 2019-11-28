import {ApplicationStatus} from '@model/application/application-status';
import {DistributionEntry} from '@model/common/distribution-entry';

export class BulkApprovalEntry {
  constructor(
    public id?: number,
    public applicationId?: string,
    public targetState?: ApplicationStatus,
    public bulkApprovalBlocked?: boolean,
    public bulkApprovalBlockedReason?: string,
    public distributionList: DistributionEntry[] = []) {}
}
