import {Inject, LOCALE_ID, Pipe, PipeTransform} from '@angular/core';
import {formatCurrency} from '@angular/common';
import {NumberUtil} from '@util/number.util';

@Pipe({name: 'centsToEuros'})
export class CentsToEurosPipe implements PipeTransform {

  constructor(@Inject(LOCALE_ID) private locale: string) {}

  transform(value: number): string {
    if (value !== undefined) {
      const inEuros = NumberUtil.toEuros(value);
      return formatCurrency(inEuros, this.locale, '€', 'EUR');
    } else {
      return '';
    }
  }
}
