import { Pipe, PipeTransform } from '@angular/core';
import {findTranslation, Params} from '@util/translations';

@Pipe({name: 'translation'})
export class TranslationPipe implements PipeTransform {
  transform(key: string | Array<string>, params?: Params): string {
    if (key) {
      return findTranslation(key, params);
    } else {
      return '';
    }
  }
}
