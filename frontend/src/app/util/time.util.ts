import * as moment from 'moment';
import {unitOfTime} from 'moment';

export const MIN_YEAR = 1972;
export const MAX_YEAR = 9999;
export const MIN_DATE: Date = new Date('1972-01-01T00:00:00');
export const MAX_DATE: Date = new Date('9999-12-31T23:59:59');
export const UI_PIPE_DATE_FORMAT = 'dd.MM.yyyy'; // Used by angular date pipe
export const UI_DATE_FORMAT = 'DD.MM.YYYY';
export const UI_DATE_TIME_FORMAT = 'DD.MM.YYYY HH:mm';
const HISTORY_DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mm:ssZ';
const HISTORY_DATE_FORMAT = 'DD.MM.YYYY';
export const WINTER_TIME_START = moment('1972-12-01');
export const WINTER_TIME_END = moment('1972-05-14');
const DAYS_IN_WEEK = 7;


/**
 * Helpers for time related UI functionality.
 */
export class TimeUtil {
  public static getUiDateString(time: Date): string {
    return time ? moment(time).format(UI_DATE_FORMAT).toString() : undefined;
  }

  public static getUiDateTimeString(time: Date): string {
    return time ? moment(time).format(UI_DATE_TIME_FORMAT).toString() : undefined;
  }

  public static getDateFromUi(dateString: string): Date {
    const m = this.toMoment(dateString);
    return m ? m.toDate() : undefined;
  }

  public static toStartDate(date: Date): Date {
    return date ? moment(date).startOf('day').toDate() : undefined;
  }

  public static toEndDate(date: Date): Date {
    return date ? moment(date).endOf('day').toDate() : undefined;
  }

  public static getStartDateFromUi(dateString: string): Date {
    return dateString ? moment(dateString, UI_DATE_FORMAT).startOf('day').toDate() : undefined;
  }

  public static getEndDateFromUi(dateString: string): Date {
    return dateString ? moment(dateString, UI_DATE_FORMAT).endOf('day').toDate() : undefined;
  }

  public static yearFromDate(date: Date): number {
    return date ? moment(date).year() : undefined;
  }

  public static datePlusWeeks(date: Date, plusWeeks: number): Date {
    const asWeeks = DAYS_IN_WEEK * plusWeeks;
    return date ? moment(date).day(asWeeks).toDate() : undefined;
  }

  public static addDays(date: Date, days: number): Date {
    return date ? moment(date).add(days, 'day').toDate() : undefined;
  }

  public static dateWithYear(date: Date, year: number): Date {
    if (date && year) {
      const baseDate = moment(date);
      return baseDate.year(year).toDate();
    } else {
      return undefined;
    }
  }

  public static dateFromBackend(dateString: string): Date {
    return dateString ? moment(dateString).toDate() : undefined;
  }

  public static dateToBackend(date: Date): string {
    return date ? date.toISOString() : undefined;
  }

  public static formatHistoryDateTimeString(dateTime: string): string {
    return dateTime ? moment(dateTime, HISTORY_DATE_TIME_FORMAT).format(HISTORY_DATE_FORMAT).toString() : undefined;
  }

  public static minimum(...dates: Date[]) {
    const moments: Array<moment.Moment> = dates.map(date => moment(date));
    return moment.min(... moments).toDate();
  }

  public static maximum(...dates: Date[]) {
    const moments: Array<moment.Moment> = dates.map(date => moment(date));
    return moment.max(... moments).toDate();
  }

  public static add(baseDate: Date = new Date(), amount: number, unit: unitOfTime.DurationConstructor): Date {
    return moment(baseDate).add(amount, unit).toDate();
  }

  public static subract(baseDate: Date = new Date(), amount: number, unit: unitOfTime.DurationConstructor): Date {
    return moment(baseDate).subtract(amount, unit).toDate();
  }

  /**
   * Returns end of given day i.e. any date 1.1.2001 would be converted to 1.1.2001 23:59.
   *
   * @param date
   * @returns {Date}  end of given day i.e. any date 1.1.2001 would be converted to 1.1.2001 23:59.
   */
  public static getEndOfDay(date: Date): Date {
    return moment(date).endOf('day').toDate();
  }

  /**
   * Returns whether first argument is before second
   *
   * @returns {boolean} true when first date is before second or given dates are undefined, otherwise false.
   */
  public static isBefore(first: Date, second: Date, granularity?: unitOfTime.StartOf): boolean {
    if (first && second) {
      return moment(first).isBefore(moment(second), granularity);
    } else {
      return true;
    }
  }

  public static isAfter(first: Date, second: Date, granularity?: unitOfTime.StartOf): boolean {
    return this.isBefore(second, first, granularity);
  }

  public static isBetweenInclusive(date: Date, start: Date, end: Date): boolean {
      return moment(date).isBetween(start, end, undefined, '[]');
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

  public static equals(left: Date, right: Date): boolean {
    return this.compareTo(left, right) === 0;
  }

  public static isSame(left: Date, right: Date, granularity: unitOfTime.StartOf): boolean {
    const lMoment = moment(left);
    const rMoment = moment(right);
    return lMoment.isSame(rMoment, granularity);
  }

  public static isInWinterTime(date: Date): boolean {
    const checked = moment(date).year(WINTER_TIME_START.year());
    return checked.isSameOrAfter(WINTER_TIME_START) || checked.isBefore(WINTER_TIME_END);
  }

  public static toWinterTimeEnd(date: Date): Date {
    const checked = moment(date).year(WINTER_TIME_START.year());
    let year = date.getFullYear();
    if (checked.isSameOrAfter(WINTER_TIME_START)) {
      year = year + 1;
    }
    return moment(WINTER_TIME_END).year(year).toDate();
  }

  private static toMoment(dateString: string, format: string = UI_DATE_FORMAT): any {
    if (dateString) {
      const m = moment(dateString, format);
      return m.isValid() ? m : undefined;
    } else {
      return undefined;
    }
  }
}
