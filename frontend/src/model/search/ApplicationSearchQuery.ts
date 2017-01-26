import {TimeUtil} from '../../util/time.util';
import {Sort} from '../common/sort';
import {ApplicationSearchQueryForm} from './ApplicationSearchQueryForm';
import {SearchQuery} from '../common/search-query';
import {Some} from '../../util/option';

export class ApplicationSearchQuery implements SearchQuery {
  public applicationId: string;
  public type: Array<string>;
  public status: Array<string>;
  public districts: Array<string>;
  public handler: Array<string>;
  public address: string;
  public applicant: string;
  public contact: string;
  public freeText: string;
  public startTime: Date;
  public endTime: Date;
  public projectId: number;
  public sort: Sort;
  public tags: Array<string>;

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

  public static from(queryForm: ApplicationSearchQueryForm, sort?: Sort) {
    let query = new ApplicationSearchQuery();
    query.applicationId = queryForm.applicationId;
    query.type = queryForm.type;
    query.status = queryForm.status;
    query.districts = Some(queryForm.districts)
      .map(ds => ds
        .map(d => d.toString()))
      .orElse([]);
    query.handler = queryForm.handler;
    query.address = queryForm.address;
    query.applicant = queryForm.applicant;
    query.contact = queryForm.contact;
    query.freeText = queryForm.freeText;
    query.startTime = TimeUtil.getDateFromUi(queryForm.startTime);
    query.endTime = TimeUtil.getDateFromUi(queryForm.endTime);
    query.tags = queryForm.tags;
    query.sort = sort;
    return query;
  }

  public static forApplicationId(id: string): ApplicationSearchQuery {
    let query = new ApplicationSearchQuery();
    query.applicationId = id;
    return query;
  }

  public static forIdAndTypes(id: string, types: Array<string>): ApplicationSearchQuery {
    let query = new ApplicationSearchQuery();
    query.applicationId = id;
    query.type = types;
    return query;
  }

  public copy(): ApplicationSearchQuery {
    let query = new ApplicationSearchQuery();
    query.applicationId = this.applicationId;
    query.type = this.type;
    query.status = this.status;
    query.districts = this.districts;
    query.handler = this.handler;
    query.address = this.address;
    query.applicant = this.applicant;
    query.contact = this.contact;
    query.freeText = this.freeText;
    query.startTime = this.startTime;
    query.endTime = this.endTime;
    query.tags = this.tags;
    return query;
  }

  public withSort(sort: Sort): ApplicationSearchQuery {
    let newQuery = this.copy();
    newQuery.sort = sort;
    return newQuery;
  }
}


