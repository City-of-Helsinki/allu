import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'keys'})
export class KeysPipe implements PipeTransform {
  transform(value: any): string[] {
    // check if object
    if (value === Object(value)) {
      return Object.keys(value);
    } else {
      return [];
    }
  }
}
