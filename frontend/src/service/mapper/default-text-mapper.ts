import {BackendDefaultText} from '../backend-model/backend-default-text';
import {DefaultText} from '../../model/application/cable-report/default-text';
import {DefaultTextType} from '../../model/application/default-text-type';
import {ApplicationType} from '../../model/application/type/application-type';

export class DefaultTextMapper {
  static mapBackend(backendDefaultText: BackendDefaultText): DefaultText {
    return new DefaultText(
      backendDefaultText.id,
      ApplicationType[backendDefaultText.applicationType],
      backendDefaultText.textType ? DefaultTextType[backendDefaultText.textType] : undefined,
      backendDefaultText.textValue);
  }

  static mapFrontend(defaultText: DefaultText): BackendDefaultText {
    return {
      id: defaultText.id,
      applicationType: ApplicationType[defaultText.applicationType],
      textType: defaultText ? DefaultTextType[defaultText.type] : undefined,
      textValue: defaultText.text
    };
  }
}
