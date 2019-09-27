import {MAX_YEAR} from '@util/time.util';

export enum RecurringType {
  NONE = 'none',
  FOR_NOW = 'forNow',
  UNTIL = 'until'
}

export function recurringTypeFromDate(date: Date): RecurringType {
  if (!date) {
    return RecurringType.NONE;
  } else if (date.getFullYear() === MAX_YEAR) {
    return RecurringType.FOR_NOW;
  } else {
    return RecurringType.UNTIL;
  }
}
