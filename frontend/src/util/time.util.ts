import {Injectable} from '@angular/core';
import * as momentLib from 'moment';

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
export const UI_DATE_FORMAT: string = 'dd.MM. yy';

/**
 * Helpers for time related UI functionality.
 */
export class TimeUtil {
  public static getUiDateString(time: Date): string {
    return time ? momentLib(time).format('DD.MM.YYYY').toString() : undefined;
  }

  public static getDateFromUi(dateString: string): Date {
    return dateString ? momentLib(dateString, 'DD.MM.YYYY').toDate() : undefined;
  }

  public static dateFromBackend(dateString: string): Date {
    return dateString ? momentLib(dateString).toDate() : undefined;
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

  private static toMoment(dateString: string): any {
    return dateString ? momentLib(dateString, 'DD.MM.YYYY') : undefined;
  }
}
