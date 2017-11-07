export enum ChargeBasisType {
  CALCULATED,
  NEGLIGENCE_FEE,
  ADDITIONAL_FEE,
  DISCOUNT
}

export const manualChargeBasisTypes = [
   // TODO: Add when implemented ChargeBasisType.DISCOUNT,
  ChargeBasisType.NEGLIGENCE_FEE,
  ChargeBasisType.ADDITIONAL_FEE
];
