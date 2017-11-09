export enum ChargeBasisType {
  CALCULATED,
  NEGLIGENCE_FEE,
  ADDITIONAL_FEE,
  DISCOUNT
}

export const manualChargeBasisTypes = [
  ChargeBasisType.DISCOUNT,
  ChargeBasisType.NEGLIGENCE_FEE,
  ChargeBasisType.ADDITIONAL_FEE
];
