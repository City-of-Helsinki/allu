import {InvoicingPeriod} from '@feature/application/invoicing/invoicing-period/invoicing-period';
import {TimeUtil} from '@util/time.util';
import {ApplicationStatus} from '@app/model/application/application-status';

export interface BackendInvoicingPeriod {
  id: number;
  applicationId: number;
  startTime: string;
  endTime: string;
  invoiced: boolean;
  invoicableStatus: ApplicationStatus;
}

export class InvoicingPeriodMapper {
  static mapBackendList(backendPeriods: BackendInvoicingPeriod[] = []): InvoicingPeriod[] {
    return backendPeriods.map(p => InvoicingPeriodMapper.mapBackend(p));
  }

  static mapBackend(backendPeriod: BackendInvoicingPeriod): InvoicingPeriod {
    return new InvoicingPeriod(
      backendPeriod.id,
      backendPeriod.applicationId,
      TimeUtil.dateFromBackend(backendPeriod.startTime),
      TimeUtil.dateFromBackend(backendPeriod.endTime),
      backendPeriod.invoiced,
      backendPeriod.invoicableStatus
    );
  }
}
