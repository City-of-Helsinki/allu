import * as momentLib from 'moment';
import {UnitOfTime} from 'moment';

// jQuery pickadate configuration: http://amsul.ca/pickadate.js/date/
export const PICKADATE_PARAMETERS = [
  {
    selectMonths: true,
    selectYears: 15,
    firstDay: 'Ma',
    format: 'dd.mm.yyyy',
    monthsFull: ['Tammikuu', 'Helmikuu', 'Maaliskuu', 'Huhtikuu', 'Toukokuu', 'Kesäkuu',
      'Heinäkuu', 'Elokuu', 'Syyskuu', 'Lokakuu', 'Marraskuu', 'Joulukuu'],
    monthsShort: ['Tammi', 'Helmi', 'Maalis', 'Huhti', 'Touko', 'Kesä', 'Heinä', 'Elo', 'Syys', 'Loka', 'Marras', 'Joulu'],
    weekdaysFull: ['Su', 'Ma', 'Ti', 'Ke', 'To', 'Pe', 'La'],
    showMonthsShort: false,
    showWeekdaysFull: true // a bit counter intuitive way to get right abbreviations to be shown in calendar
  }];

export const MIN_DATE: Date = new Date(0);
export const MAX_DATE: Date = new Date('2099-12-31T23:59:59');
export const UI_DATE_FORMAT: string = 'dd.MM.yyyy'; // Used by angular date pipe
export const UI_DATE_TIME_FORMAT: string = 'DD.MM.YYYY HH:mm';
const HISTORY_DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mm:ssZ';
const HISTORY_DATE_FORMAT = 'DD.MM.YYYY';

/**
 * Helpers for time related UI functionality.
 */
export class TimeUtil {
  public static getUiDateString(time: Date): string {
    return time ? momentLib(time).format('DD.MM.YYYY').toString() : undefined;
  }

  public static getUiDateTimeString(time: Date): string {
    return time ? momentLib(time).format(UI_DATE_TIME_FORMAT).toString() : undefined;
  }

  public static getDateFromUi(dateString: string): Date {
    return dateString ? momentLib(dateString, 'DD.MM.YYYY').toDate() : undefined;
  }

  public static getStartDateFromUi(dateString: string): Date {
    return dateString ? momentLib(dateString, 'DD.MM.YYYY').startOf('day').toDate() : undefined;
  }

  public static getEndDateFromUi(dateString: string): Date {
    return dateString ? momentLib(dateString, 'DD.MM.YYYY').endOf('day').toDate() : undefined;
  }

  public static dateFromBackend(dateString: string): Date {
    return dateString ? momentLib(dateString).toDate() : undefined;
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

  public static dateToMoment(date: Date): any {
    return date ? momentLib(date) : undefined;
  }

  private static toMoment(dateString: string): any {
    return dateString ? momentLib(dateString, 'DD.MM.YYYY') : undefined;
  }
}
