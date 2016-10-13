import {TimeUtil} from '../../util/time.util';
import {Sort} from '../common/sort';
import {ApplicationSearchQueryForm} from './ApplicationSearchQueryForm';

export class ApplicationSearchQuery {
  public applicationId: string;
  public type: string;
  public status: string;
  public handler: string;
  public address: string;
  public applicant: string;
  public contact: string;
  public freeText: string;
  public startTime: Date;
  public endTime: Date;
  public sort: Sort;

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

  public static from(queryForm: ApplicationSearchQueryForm) {
    let query = new ApplicationSearchQuery();
    query.applicationId = queryForm.applicationId;
    query.type = queryForm.type;
    query.status = queryForm.status;
    query.handler = queryForm.handler;
    query.address = queryForm.address;
    query.applicant = queryForm.applicant;
    query.contact = queryForm.contact;
    query.freeText = queryForm.freeText;
    query.startTime = TimeUtil.getDateFromUi(queryForm.startTime);
    query.endTime = TimeUtil.getDateFromUi(queryForm.endTime);
    return query;
  }

  public copy(): ApplicationSearchQuery {
    let query = new ApplicationSearchQuery();
    query.applicationId = this.applicationId;
    query.type = this.type;
    query.status = this.status;
    query.handler = this.handler;
    query.address = this.address;
    query.applicant = this.applicant;
    query.contact = this.contact;
    query.freeText = this.freeText;
    query.startTime = this.startTime;
    query.endTime = this.endTime;
    return query;
  }

  public withSort(sort: Sort): ApplicationSearchQuery {
    let newQuery = this.copy();
    newQuery.sort = sort;
    return newQuery;
  }
}


