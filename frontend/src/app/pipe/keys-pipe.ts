import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'keys'})
export class KeysPipe implements PipeTransform {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  transform(value: any): string[] {
    // check if object
    if (value === Object(value)) {
      return Object.keys(value);
    } else {
      return [];
    }
  }
}
