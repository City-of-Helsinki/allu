import {NativeDateAdapter} from '@angular/material/core';
import {TimeUtil} from './time.util';
import { Injectable } from "@angular/core";

const DEFAULT_MONTH_NAMES = {
  'long': [
    'Tammikuu', 'Helmikuu', 'Maaliskuu', 'Huhtikuu', 'Toukokuu', 'Kesäkuu',
    'Heinäkuu', 'Elokuu', 'Syyskuu', 'Lokakuu', 'Marraskuu', 'Joulukuu'
  ],
  'short': ['Tam', 'Hel', 'Maa', 'Huh', 'Tou', 'Kes', 'Hei', 'Elo', 'Syy', 'Lok', 'Mar', 'Jou'],
  'narrow': ['T', 'H', 'M', 'H', 'T', 'K', 'H', 'E', 'S', 'L', 'M', 'J']
};

const DEFAULT_DAY_OF_WEEK_NAMES = {
  'long': ['Sunnuntai', 'Maanantai', 'Tiistai', 'Keskiviikko', 'Torstai', 'Perjantai', 'Lauantai'],
  'short': ['Su', 'Ma', 'Ti', 'Ke', 'To', 'Pe', 'La'],
  'narrow': ['S', 'M', 'T', 'K', 'T', 'P', 'L']
};

const MONDAY_INDEX = 1;

@Injectable()
export class AlluDateAdapter extends NativeDateAdapter {

  getMonthNames(style): string[] {
    return DEFAULT_MONTH_NAMES[style];
  }

  getDayOfWeekNames(style): string[] {
    return DEFAULT_DAY_OF_WEEK_NAMES[style];
  }

  format(date: Date, displayFormat: Object): string {
    return TimeUtil.getUiDateString(date);
  }


  parse(value: any): Date | any {
    return typeof value === 'string' ? TimeUtil.getDateFromUi(value) : value;
  }

  getFirstDayOfWeek(): number {
    return MONDAY_INDEX;
  }
}
