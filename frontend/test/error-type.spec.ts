import './../src/service/ui-state/error-type.ts';
import {ErrorType, message} from '../src/service/ui-state/error-type';
import {translations} from '../src/util/translations';
import {EnumUtil} from '../src/util/enum.util';

describe('ErrorType', () => {
  it('should return expected message', () =>
    expect(message(ErrorType.APPLICATION_SEARCH_FAILED)).toBe(translations.application.error.searchFailed));

  it('should have messages for all Error types', () => {
    EnumUtil.enumValues(ErrorType).forEach(type => {
      expect(message(ErrorType[type])).toBeDefined('Missing message for type ' + type);
    });
  });
});
