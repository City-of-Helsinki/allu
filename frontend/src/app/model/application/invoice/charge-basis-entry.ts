import {ChargeBasisUnit} from './charge-basis-unit';
import {ChargeBasisType} from './charge-basis-type';

export class ChargeBasisEntry {
  constructor(
    public id?: number,
    public type?: ChargeBasisType,
    public unit?: ChargeBasisUnit,
    public quantity?: number,
    public text?: string,
    public unitPrice?: number,
    public netPrice?: number,
    public manuallySet?: boolean,
    public tag?: string,
    public referredTag?: string,
    public explanation: string[] = [],
    public locked?: boolean,
    public referrable?: boolean
  ) {}
}
