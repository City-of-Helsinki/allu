import {ApplicationSpecifier} from '../app/model/application/type/application-specifier';
import {EnumUtil} from '../app/util/enum.util';
import {translations, findTranslation} from '../app/util/translations';

describe('translations', () => {
  it('should have translations for all application specifiers', () => {
    EnumUtil.enumValues(ApplicationSpecifier).forEach(specifier => {
      expect(translations.application.specifier[specifier]).toBeDefined('Missing translation for specifier' + specifier);
    });
  });

  it('should find translation by path', () => {
    expect(findTranslation('application.status.HANDLING')).toEqual('Käsittelyssä');
  });

  it('should find return path if no translation is found', () => {
    expect(findTranslation('this.should.not.exits')).toEqual('this.should.not.exits');
  });

  it('should allow path parts in array', () => {
    expect(findTranslation(['application.status', 'HANDLING'])).toEqual('Käsittelyssä');
    expect(findTranslation(['application', 'status', 'HANDLING'])).toEqual('Käsittelyssä');
    expect(findTranslation(['application.status.HANDLING'])).toEqual('Käsittelyssä');
  });

  it('should return key if only part of the path is valid', () => {
    expect(findTranslation(['application.status', 'SHOULD_NOT_EXITS'])).toEqual('application.status.SHOULD_NOT_EXITS');
  });

  it('should translate with parameters', () => {
    const from = {withParams: 'this is with {{param1}} given param'};
    expect(findTranslation(['withParams'], {param1: 'replaced'}, from))
      .toEqual('this is with replaced given param');
  });

  it('should translate when parameter not found', () => {
    const from = {withParams: 'this is with given param'};
    expect(findTranslation(['withParams'], {param1: 'replaced'}, from))
      .toEqual('this is with given param');
  });
});
