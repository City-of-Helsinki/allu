import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';

export class Note extends ApplicationExtension {
  public applicationType = ApplicationType[ApplicationType.NOTE];

  constructor(
    public reoccurring?: boolean,
    public description?: string
  ) {
    super();
  }
}
