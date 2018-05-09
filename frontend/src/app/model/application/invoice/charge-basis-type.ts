export enum ChargeBasisType {
  CALCULATED,
  AREA_USAGE_FEE,
  NEGLIGENCE_FEE,
  ADDITIONAL_FEE,
  DISCOUNT
}

export const manualChargeBasisTypes = [
  ChargeBasisType.DISCOUNT,
  ChargeBasisType.NEGLIGENCE_FEE,
  ChargeBasisType.ADDITIONAL_FEE,
  ChargeBasisType.AREA_USAGE_FEE
];
