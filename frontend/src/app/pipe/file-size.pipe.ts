import {Pipe, PipeTransform, Inject, LOCALE_ID} from '@angular/core';
import {formatNumber} from '@angular/common';

export type SizeUnit = 'B' | 'kB' | 'MB' | 'GB' | 'TB';

interface ConversionConfig {
  multiplier: number;
  prev?: SizeUnit;
}

const byteIn: {[key: string]: ConversionConfig} = {
  B: {multiplier: 1},
  kB: {multiplier: 1024, prev: 'B'},
  MB: {multiplier: Math.pow(1024, 2), prev: 'kB'},
  GB: {multiplier: Math.pow(1024, 3), prev: 'MB'},
  TB: {multiplier: Math.pow(1024, 4), prev: 'GB'}
};

@Pipe({name: 'fileSize'})
export class FileSizePipe implements PipeTransform {

  constructor(@Inject(LOCALE_ID) private locale: string) {}

  transform(value: number, unit: SizeUnit = 'B'): string {
    if (value !== undefined) {
      const largestVisible = this.largestVisibleUnit(value, unit);
      const valueInUnit = value / byteIn[largestVisible].multiplier;
      const formattedValue = formatNumber(valueInUnit, this.locale, '1.0-1');
      return `${formattedValue} ${largestVisible}`;
    } else {
      return '';
    }
  }

  private largestVisibleUnit(value: number, requestedUnit: SizeUnit): SizeUnit {
    const format = byteIn[requestedUnit];
    if (value < format.multiplier && format.prev) {
      return this.largestVisibleUnit(value, format.prev);
    } else {
      return requestedUnit;
    }
  }
}
