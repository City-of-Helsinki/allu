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

/**
 * Helpers for time related UI functionality.
 */
export class TimeUtil {
  public static getUiDateString(time: Date): string {
    return momentLib(time).format('DD.MM.YYYY').toString();
  }

  public static getDateFromUi(dateString: string): Date {
    return momentLib(dateString, 'DD.MM.YYYY').toDate();
  }
}
