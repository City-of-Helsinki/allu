import {DepositStatusType} from './deposit-status-type';
import {NumberUtil} from '../../../util/number.util';

export class Deposit {
  constructor(
    public id?: number,
    public applicationId?: number,
    public amount?: number,
    public reason?: string,
    public status: DepositStatusType = DepositStatusType.UNPAID_DEPOSIT) {
  }

  get amountEuro(): number {
    return NumberUtil.toEuros(this.amount);
  }

  set amountEuro(amount: number) {
    this.amount = NumberUtil.toCents(amount);
  }

  get uiStatus(): string {
    return DepositStatusType[this.status];
  }

  set uiStatus(status: string) {
    this.status = DepositStatusType[status];
  }

  static forApplication(id: number): Deposit {
    return new Deposit(undefined, id);
  }

  static toForm(deposit: Deposit): DepositForm {
    return {
      id: deposit.id,
      applicationId: deposit.applicationId,
      amount: deposit.amountEuro,
      reason: deposit.reason,
      status: deposit.uiStatus
    };
  }

  static fromForm(form: DepositForm): Deposit {
    const deposit = new Deposit();
    deposit.id = form.id;
    deposit.applicationId = form.applicationId;
    deposit.amountEuro = form.amount;
    deposit.reason = form.reason;
    deposit.uiStatus = form.status;
    return deposit;
  }
}

export interface DepositForm {
  id: number;
  applicationId: number;
  amount: number;
  reason: string;
  status: string;
}
