export interface BackendChargeBasisEntry {
  unit: string;
  quantity: number;
  text: string;
  unitPrice: number;
  netPrice: number;
  manuallySet: boolean;
  tag: string;
  referredTag: string;
}
