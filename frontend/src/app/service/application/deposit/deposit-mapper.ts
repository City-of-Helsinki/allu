import {BackendDeposit} from './backend-deposit';
import {Deposit} from '../../../model/application/invoice/deposit';
import {DepositStatusType} from '../../../model/application/invoice/deposit-status-type';
import {Some} from '../../../util/option';

export class DepositMapper {

  static mapBackend(deposit: BackendDeposit): Deposit {
    return new Deposit(
      deposit.id,
      deposit.applicationId,
      deposit.amount,
      deposit.reason,
      Some(deposit.status).map(status => DepositStatusType[status]).orElse(undefined)
    );
  }

  static mapFrontend(deposit: Deposit): BackendDeposit {
    return (deposit) ?
      {
        id: deposit.id,
        applicationId: deposit.applicationId,
        amount: deposit.amount,
        reason: deposit.reason,
        status: Some(deposit.status).map(status => DepositStatusType[status]).orElse(undefined)
      }
      : undefined;
  }
}

