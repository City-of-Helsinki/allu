import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'commaSeparated'})
export class CommaSeparatedPipe implements PipeTransform {
  transform(value: string | Array<string>, isLast: boolean): string {
    if (value instanceof Array) {
      return value.join(', ');
    } else {
      const last = isLast && isLast === true;
      return last ? value : (value + ', ');
    }
  }
}
