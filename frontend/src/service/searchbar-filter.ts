import {TimeUtil} from '../util/time.util';

export class SearchbarFilter {
  constructor(
    public search?: string,
    public startDate?: Date,
    public endDate?: Date) {}

  public get uiStartDate(): string {
    return TimeUtil.getUiDateString(this.startDate);
  }

  public get uiEndDate(): string {
    return TimeUtil.getUiDateString(this.endDate);
  }
}
