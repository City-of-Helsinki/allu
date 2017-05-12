import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';

export class Note extends ApplicationExtension {
  constructor(
    public description?: string
  ) {
    super(ApplicationType[ApplicationType.NOTE]);
  }
}
