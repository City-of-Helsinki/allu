// Describes application types which have payment tariff regardless of kind
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationKind} from '@model/application/type/application-kind';
import {ArrayUtil} from '@util/array-util';

export const typesWithPaymentTariff: ApplicationType[] = [
  ApplicationType.EXCAVATION_ANNOUNCEMENT,
  ApplicationType.AREA_RENTAL
];

export const kindsWithPaymentTariff: ApplicationKind[] = [
  ApplicationKind.SUMMER_TERRACE,
  ApplicationKind.WINTER_TERRACE,
  ApplicationKind.PARKLET
];

export const defaultPaymentTariffs = ['1', '2', '3', '4a', '4b'];
export const terraceAndParkletPaymentTariffs = ['1', '2'];

export function needsPaymentTariff(applicationType: ApplicationType, kinds: ApplicationKind[] = []): boolean {
  return ArrayUtil.contains(typesWithPaymentTariff, applicationType)
    || ArrayUtil.anyMatch(kindsWithPaymentTariff, kinds);
}

export function getPaymentTariffs(kinds: ApplicationKind[] = []): string[] {
  if (ArrayUtil.anyMatch(kindsWithPaymentTariff, kinds)) {
    return terraceAndParkletPaymentTariffs;
  } else {
    return defaultPaymentTariffs;
  }
}
