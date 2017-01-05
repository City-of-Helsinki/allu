import {TimeUtil} from '../../../util/time.util';

export class AttachmentInfo {
  constructor(
    public id?: number,
    public type?: string,
    public name?: string,
    public description?: string,
    public size?: number,
    public creationTime?: Date,
    public handlerName?: string,
    public file?: any
  ) {};

  get uiCreationTime(): string {
    return TimeUtil.getUiDateString(this.creationTime);
  }
}
