import './../src/app/service/ui-state/error-type.ts';
import {ErrorType, messageToReadable} from '../src/app/service/ui-state/error-type';
import {translations} from '../src/app/util/translations';
import {EnumUtil} from '../src/app/util/enum.util';

describe('ErrorType', () => {
  it('should return expected message', () =>
    expect(messageToReadable(ErrorType.APPLICATION_SEARCH_FAILED)).toBe(translations.application.error.searchFailed));

  it('should have messages for all Error types', () => {
    EnumUtil.enumValues(ErrorType).forEach(type => {
      expect(messageToReadable(ErrorType[type])).toBeDefined('Missing message for type ' + type);
    });
  });
});
