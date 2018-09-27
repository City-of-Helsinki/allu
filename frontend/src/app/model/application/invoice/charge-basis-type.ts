export enum ChargeBasisType {
  CALCULATED = 'CALCULATED',
  AREA_USAGE_FEE = 'AREA_USAGE_FEE',
  NEGLIGENCE_FEE = 'NEGLIGENCE_FEE',
  ADDITIONAL_FEE = 'ADDITIONAL_FEE',
  DISCOUNT = 'DISCOUNT'
}

export const manualChargeBasisTypes = [
  ChargeBasisType.DISCOUNT,
  ChargeBasisType.NEGLIGENCE_FEE,
  ChargeBasisType.ADDITIONAL_FEE,
  ChargeBasisType.AREA_USAGE_FEE
];
