import * as momentLib from 'moment';
import {UnitOfTime} from 'moment';

export const MIN_YEAR = 1972;
export const MAX_YEAR = 9999;
export const MIN_DATE: Date = new Date('1972-01-01T00:00:00');
export const MAX_DATE: Date = new Date('9999-12-31T23:59:59');
export const UI_PIPE_DATE_FORMAT: string = 'dd.MM.yyyy'; // Used by angular date pipe
export const UI_DATE_FORMAT: string = 'DD.MM.YYYY';
export const UI_DATE_TIME_FORMAT: string = 'DD.MM.YYYY HH:mm';
const HISTORY_DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mm:ssZ';
const HISTORY_DATE_FORMAT = 'DD.MM.YYYY';

/**
 * Helpers for time related UI functionality.
 */
export class TimeUtil {
  public static getUiDateString(time: Date): string {
    return time ? momentLib(time).format(UI_DATE_FORMAT).toString() : undefined;
  }

  public static getUiDateTimeString(time: Date): string {
    return time ? momentLib(time).format(UI_DATE_TIME_FORMAT).toString() : undefined;
  }

  public static getDateFromUi(dateString: string): Date {
    let m = this.toMoment(dateString);
    return m ? m.toDate() : undefined;
  }

  public static getStartDateFromUi(dateString: string): Date {
    return dateString ? momentLib(dateString, UI_DATE_FORMAT).startOf('day').toDate() : undefined;
  }

  public static getEndDateFromUi(dateString: string): Date {
    return dateString ? momentLib(dateString, UI_DATE_FORMAT).endOf('day').toDate() : undefined;
  }

  public static yearFromDate(date: Date): number {
    return date ? momentLib(date).year() : undefined;
  }

  public static dateWithYear(date: Date, year: number): Date {
    if (date && year) {
      let baseDate = momentLib(date);
      return baseDate.year(year).toDate();
    } else {
      return undefined;
    }
  }

  public static dateFromBackend(dateString: string): Date {
    return dateString ? momentLib(dateString).toDate() : undefined;
  }

  public static dateToBackend(date: Date): string {
    return date ? date.toISOString() : undefined;
  }

  public static formatHistoryDateTimeString(dateTime: string): string {
    return dateTime ? momentLib(dateTime, HISTORY_DATE_TIME_FORMAT).format(HISTORY_DATE_FORMAT).toString() : undefined;
  }

  public static minimum(...dates: Date[]) {
    let moments: Array<momentLib.Moment> = dates.map(date => momentLib(date));
    return momentLib.min(... moments).toDate();
  }

  public static maximum(...dates: Date[]) {
    let moments: Array<momentLib.Moment> = dates.map(date => momentLib(date));
    return momentLib.max(... moments).toDate();
  }

  public static add(baseDate: Date = new Date(), amount: number, unit: UnitOfTime): Date {
    return momentLib(baseDate).add(amount, unit).toDate();
  }

  /**
   * Returns end of given day i.e. any date 1.1.2001 would be converted to 1.1.2001 23:59.
   *
   * @param date
   * @returns {Date}  end of given day i.e. any date 1.1.2001 would be converted to 1.1.2001 23:59.
   */
  public static getEndOfDay(date: Date): Date {
    return momentLib(date).endOf('day').toDate();
  }

  /**
   * Returns whether first argument is before second
   *
   * @param first date as string
   * @param second date as string
   * @returns {boolean} true when first date is before second or given strings are undefined, otherwise false.
   */
  public static isBefore(first: string, second: string): boolean {
    if (!!first && !!second) {
      return TimeUtil.toMoment(first).isBefore(TimeUtil.toMoment(second));
    } else {
      return true;
    }

  }

  public static isBetweenInclusive(date: Date, start: Date, end: Date): boolean {
      return momentLib(date).isBetween(start, end, undefined, '[]');
  }

  public static compareTo(left: Date, right: Date): number {
    if (left > right) {
      return 1;
    } else if (left < right) {
      return -1;
    } else {
      return 0;
    }
  }

  private static toMoment(dateString: string, format: string = UI_DATE_FORMAT): any {
    if (dateString) {
      let m = momentLib(dateString, format);
      return m.isValid() ? m : undefined;
    } else {
      return undefined;
    };
  }
}
