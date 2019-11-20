import {Application} from '@model/application/application';
import {ApplicationStatus} from '@model/application/application-status';
import {NumberUtil} from '@util/number.util';
import {Some} from '@util/option';

export type DecisionBlockedReason = 'requiredInvoicingInfoMissing' | 'changesNotHandled';

const validStatusForInformationRequest = [
  ApplicationStatus.PENDING,
  ApplicationStatus.HANDLING,
  ApplicationStatus.RETURNED_TO_PREPARATION
];

export class ApplicationUtil {
  public static validForInformationRequest(app: Application): boolean {
    const status = app.status;
    const validStatus = validStatusForInformationRequest.indexOf(status) >= 0;
    const external =  NumberUtil.isDefined(app.externalOwnerId);
    return validStatus && external;
  }
}

export function validInvoicingForDecision(app: Application, hasInvoicing: boolean): boolean {
  return app.notBillable || (NumberUtil.isDefined(app.invoiceRecipientId) && hasInvoicing);
}

export function decisionBlockedByReasons(app: Application, hasInvoicing: boolean, hasBlockingTags: boolean): DecisionBlockedReason[] {
  const validInvoicing = validInvoicingForDecision(app, hasInvoicing);

  return Some([])
    .map(reasons => validInvoicing ? reasons : reasons.concat('requiredInvoicingInfoMissing'))
    .map(reasons => !hasBlockingTags ? reasons : reasons.concat('changesNotHandled'))
    .orElse([]);
}
