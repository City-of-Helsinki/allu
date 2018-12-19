import {BackendDefaultText} from '../backend-model/backend-default-text';
import {DefaultText} from '@model/application/cable-report/default-text';

export class DefaultTextMapper {
  static mapBackend(backendDefaultText: BackendDefaultText): DefaultText {
    return new DefaultText(
      backendDefaultText.id,
      backendDefaultText.applicationType,
      backendDefaultText.textType,
      backendDefaultText.textValue);
  }

  static mapFrontend(defaultText: DefaultText): BackendDefaultText {
    return {
      id: defaultText.id,
      applicationType: defaultText.applicationType,
      textType: defaultText.type,
      textValue: defaultText.text
    };
  }
}
