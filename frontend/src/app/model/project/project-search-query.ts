import {TimeUtil} from '../../util/time.util';
import {SearchQuery} from '../common/search-query';
import {Sort} from '../common/sort';

export class ProjectSearchQuery implements SearchQuery {
  public id: number;
  public identifier: string;
  public startTime: Date;
  public endTime: Date;
  public ownerName: string;
  public onlyActive: boolean;
  public districts: Array<string>;
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

  static fromForm(form: ProjectSearchQueryForm, sort?: Sort): ProjectSearchQuery {
    const query = new ProjectSearchQuery();
    query.id = form.id;
    query.identifier = form.identifier;
    query.startTime = form.startTime;
    query.endTime = form.endTime;
    query.ownerName = form.ownerName;
    query.onlyActive = form.onlyActive;
    query.districts = form.districts;
    query.creator = form.creator;
    query.sort = sort;
    return query;
  }

  static fromProjectId(id: number): ProjectSearchQuery {
    const query = new ProjectSearchQuery();
    query.id = id;
    return query;
  }
}

interface ProjectSearchQueryForm {
  id: number;
  identifier: string;
  startTime: Date;
  endTime: Date;
  ownerName: string;
  onlyActive: boolean;
  districts: Array<string>;
  creator: number;
}
