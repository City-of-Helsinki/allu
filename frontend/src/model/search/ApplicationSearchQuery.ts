
import {TimeUtil} from '../../util/time.util';
export class ApplicationSearchQuery {
  public name: string;
  public type: string;
  public status: string;
  public handler: string;
  public address: string;
  public applicant: string;
  public contact: string;
  public freeText: string;
  public startTime: Date;
  public endTime: Date;

  public get uiStartTime(): string {
    return TimeUtil.getUiDateString(this.startTime);
  }

  public set uiStartTime(dateString: string) {
    this.startTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiEndTime(): string {
    return TimeUtil.getUiDateString(this.endTime);
  }

  public set uiEndTime(dateString: string) {
    this.endTime = TimeUtil.getDateFromUi(dateString);
  }
}
