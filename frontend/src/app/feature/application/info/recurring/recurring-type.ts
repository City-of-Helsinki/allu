import {DATE_MONTH_FORMAT, MAX_YEAR, TimeUtil} from '@util/time.util';
import {formatDate} from '@angular/common';
import {findTranslation} from '@util/translations';

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

export function recurringDateString(startTime: Date, endTime: Date, localeId: string, recurringEndTime?: Date): string {
  const recurringType = recurringTypeFromDate(recurringEndTime);
  switch (recurringType) {
    case RecurringType.UNTIL: {
      const description = findTranslation(
        'application.recurring.betweenYears',
        {startYear: startTime.getFullYear(), endYear: recurringEndTime.getFullYear()}
      );
      return recurringDateStringWithDescription(startTime, endTime, localeId, description);
    }
    case RecurringType.FOR_NOW: {
      const description = findTranslation(
        'application.recurring.recurringFromYear',
        {startYear: startTime.getFullYear()}
      );
      return recurringDateStringWithDescription(startTime, endTime, localeId, description);
    }
    default:
      return `${TimeUtil.getUiDateString(startTime)} - ${TimeUtil.getUiDateString(endTime)}`;
  }
}

function recurringDateStringWithDescription(startTime: Date, endTime: Date, localeId: string, description: string): string {
  const startDayMonth = formatDate(startTime, DATE_MONTH_FORMAT, localeId);
  const endDayMonth = formatDate(endTime, DATE_MONTH_FORMAT, localeId);
  return `${startDayMonth} - ${endDayMonth} ${description}`;
}
