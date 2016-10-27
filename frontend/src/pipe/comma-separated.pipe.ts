import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'commaSeparated'})
export class CommaSeparatedPipe implements PipeTransform {
  transform(value: string, isLast: boolean): string {
    let last = isLast && isLast === true;
    return last ? value : (value + ', ');
  }
}
