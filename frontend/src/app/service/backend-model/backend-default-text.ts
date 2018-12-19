import {ApplicationType} from '@model/application/type/application-type';
import {DefaultTextType} from '@model/application/default-text-type';

export interface BackendDefaultText {
  id: number;
  applicationType: ApplicationType;
  textType: DefaultTextType;
  textValue: string;
}
