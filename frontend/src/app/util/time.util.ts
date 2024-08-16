import * as moment from 'moment';
import {unitOfTime} from 'moment';

export const MIN_YEAR = 1973;
export const MAX_YEAR = 9999;
export const MIN_DATE: Date = new Date('1973-01-01T00:00:00');
export const MAX_DATE: Date = new Date('9999-12-31T23:59:59');
export const UI_PIPE_DATE_FORMAT = 'dd.MM.yyyy'; // Used by angular date pipe
export const UI_DATE_FORMAT = 'DD.MM.YYYY';
export const UI_DATE_TIME_FORMAT = 'DD.MM.YYYY HH:mm';
export const DATE_MONTH_FORMAT = 'dd.MM.';
const HISTORY_DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mm:ssZ';
const HISTORY_DATE_FORMAT = 'DD.MM.YYYY';
const DAYS_IN_WEEK = 7;


/**
 * Helpers for time related UI functionality.
 */
export class TimeUtil {
  public static getDateString(date: Date, format: string): string {
    return date ? moment(date).format(format).toString() : undefined;
  }

  public static getUiMonth(date: Date): number {
    return date ? date.getMonth() + 1 : 1;
  }

  public static fromUiMonth(number): number {
    return number - 1;
  }

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

  public static getEndOfMonth(month: number): number {
    const endOfMonth = moment([MIN_YEAR, this.fromUiMonth(month), 1]).endOf('month');
    return endOfMonth.date();
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

  public static getStartOfDay(date: Date): Date {
    return moment(date).startOf('day').toDate();
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
    const leftMillis = !!left ? left.getTime() : undefined;
    const rightMillis = !!right ? right.getTime() : undefined;

    if (leftMillis === rightMillis) {
      return 0;
    } else if (leftMillis === undefined) {
      return 1;
    } else if (right === undefined) {
      return -1;
    } else if (leftMillis > rightMillis) {
      return 1;
    } else {
      return -1;
    }
  }

  public static equals(left: Date, right: Date): boolean {
    return this.compareTo(left, right) === 0;
  }

  public static isSame(left: Date, right: Date, granularity: unitOfTime.StartOf): boolean {
    if (left === right) {
      return true;
    } else if (!right || !left) {
      return false;
    } else {
      const lMoment = moment(left);
      const rMoment = moment(right);
      return lMoment.isSame(rMoment, granularity);
    }
  }

  public static isInTimePeriod(date: Date, periodStart: Date, periodEnd: Date): boolean {
    if (!date || !periodStart || !periodEnd) {
      return false;
    }

    const start = moment(periodStart);
    const winterEndDate = this.toTimePeriodEnd(date, periodEnd);
    let winterStartYear = winterEndDate.getFullYear();
    if (start.month() > winterEndDate.getMonth()) {
      winterStartYear = winterEndDate.getFullYear() - 1;
    }
    return this.isBetweenInclusive(date, start.year(winterStartYear).toDate(), winterEndDate); 
  }

  public static toTimePeriodEnd(date: Date, periodEnd: Date): Date {
    if (date && periodEnd) {
      let year = date.getFullYear();
      if (date.getMonth() > periodEnd.getMonth()) {
        year = year + 1;
      }
      return this.getEndOfDay(moment(periodEnd).year(year).toDate());
    } else {
      return undefined;
    }
  }

  public static toTimePeriodStart(date: Date, periodStart: Date): Date {
    if (date && periodStart) {
      let year = date.getFullYear();
      if (date.getMonth() < periodStart.getMonth()) {
        year = year - 1;
      }
      return this.getStartOfDay(moment(periodStart).year(year).toDate());
    } else {
      return undefined;
    }
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
