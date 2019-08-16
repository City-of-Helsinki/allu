/**
 * Class to wrap errors
 */
import {findTranslation} from '@util/translations';

export class ErrorInfo {
  constructor(public title: string, public message?: string) {}
}

export function createTranslated(titleKey: string, messageKey?: string): ErrorInfo {
  const title = findTranslation(titleKey);
  const message = findTranslation(messageKey);
  return new ErrorInfo(title, message);
}
