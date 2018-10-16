import {ApplicationTagType} from '@model/application/tag/application-tag-type';

export class ApplicationTag {
  constructor(
    public type?: ApplicationTagType,
    public addedBy?: number,
    public creationTime?: Date
  ) {}
}
