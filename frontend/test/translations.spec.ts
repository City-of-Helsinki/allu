import {ApplicationSpecifier} from '../src/model/application/type/application-specifier';
import {EnumUtil} from '../src/util/enum.util';
import {translations} from '../src/util/translations';

describe('translations', () => {
  it('should have translations for all application specifiers', () => {
    EnumUtil.enumValues(ApplicationSpecifier).forEach(specifier => {
      expect(translations.application.specifier[specifier]).toBeDefined('Missing translation for specifier' + specifier);
    });
  });
});
