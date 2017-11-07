export interface BackendChargeBasisEntry {
  type: string;
  unit: string;
  quantity: number;
  text: string;
  unitPrice: number;
  netPrice: number;
  manuallySet: boolean;
  tag: string;
  referredTag: string;
  explanation: string[];
}
