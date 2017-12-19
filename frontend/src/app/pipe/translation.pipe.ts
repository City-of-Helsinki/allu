import { Pipe, PipeTransform } from '@angular/core';
import {findTranslation} from '../util/translations';

@Pipe({name: 'translation'})
export class TranslationPipe implements PipeTransform {
  transform(key: string | Array<string>): string {
    if (key) {
      return findTranslation(key);
    } else {
      return '';
    }
  }
}
