import {TimeUtil} from '../../util/time.util';
import {SearchQuery} from '../common/search-query';
import {Sort} from '../common/sort';

export class ProjectSearchQuery implements SearchQuery {
  public id: number;
  public startTime: Date;
  public endTime: Date;
  public ownerName: string;
  public status: string;
  public district: string;
  public creator: number;
  public sort: Sort;

  get uiStartTime(): string {
    return TimeUtil.getUiDateString(this.startTime);
  }

  set uiStartTime(dateString: string) {
    this.startTime = TimeUtil.getDateFromUi(dateString);
  }

  get uiEndTime(): string {
    return TimeUtil.getUiDateString(this.endTime);
  }

  set uiEndTime(dateString: string) {
    this.endTime = TimeUtil.getDateFromUi(dateString);
  }

  static fromForm(form: ProjectSearchQueryForm): ProjectSearchQuery {
    let query = new ProjectSearchQuery();
    query.id = form.id;
    query.uiStartTime = form.startTime;
    query.uiEndTime = form.endTime;
    query.ownerName = form.ownerName;
    query.status = form.status;
    query.district = form.district;
    query.creator = form.creator;
    return query;
  }
}

interface ProjectSearchQueryForm {
  id: number;
  startTime: string;
  endTime: string;
  ownerName: string;
  status: string;
  district: string;
  creator: number;
}
