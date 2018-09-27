import {ChargeBasisUnit} from '@model/application/invoice/charge-basis-unit';
import {ChargeBasisType} from '@model/application/invoice/charge-basis-type';

export interface BackendChargeBasisEntry {
  id: number;
  type: ChargeBasisType;
  unit: ChargeBasisUnit;
  quantity: number;
  text: string;
  unitPrice: number;
  netPrice: number;
  manuallySet: boolean;
  tag: string;
  referredTag: string;
  explanation: string[];
  locked?: boolean;
  referrable?: boolean;
}
