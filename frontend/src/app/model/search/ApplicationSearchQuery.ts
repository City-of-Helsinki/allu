import {TimeUtil} from '../../util/time.util';
import {Sort} from '../common/sort';
import {ApplicationSearchQueryForm} from './ApplicationSearchQueryForm';
import {SearchQuery} from '../common/search-query';
import {Some} from '../../util/option';

export class ApplicationSearchQuery implements SearchQuery {
  public applicationId: string;
  public name: string;
  public type: Array<string> = [];
  public status: Array<string> = [];
  public districts: Array<number> = [];
  public owner: Array<string> = [];
  public address: string;
  public applicant: string;
  public contact: string;
  public freeText: string;
  public startTime: Date;
  public endTime: Date;
  public projectId: number;
  public sort: Sort;
  public tags: Array<string> = [];

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
    const query = new ApplicationSearchQuery();
    query.applicationId = queryForm.applicationId;
    query.name = queryForm.name;
    query.type = queryForm.type;
    query.status = queryForm.status;
    query.districts = Some(queryForm.districts).orElse([]);
    query.owner = queryForm.owner;
    query.address = queryForm.address;
    query.applicant = queryForm.applicant;
    query.contact = queryForm.contact;
    query.freeText = queryForm.freeText;
    query.startTime = queryForm.startTime;
    query.endTime = queryForm.endTime;
    query.tags = queryForm.tags;
    query.sort = sort;
    return query;
  }

  public static forIdAndTypes(id: string, types: Array<string>): ApplicationSearchQuery {
    const query = new ApplicationSearchQuery();
    query.applicationId = id;
    query.type = types;
    return query;
  }

  public copy(): ApplicationSearchQuery {
    const query = new ApplicationSearchQuery();
    query.applicationId = this.applicationId;
    query.type = this.type;
    query.status = this.status;
    query.districts = this.districts;
    query.owner = this.owner;
    query.address = this.address;
    query.applicant = this.applicant;
    query.contact = this.contact;
    query.freeText = this.freeText;
    query.startTime = this.startTime;
    query.endTime = this.endTime;
    query.tags = this.tags;
    return query;
  }
}


