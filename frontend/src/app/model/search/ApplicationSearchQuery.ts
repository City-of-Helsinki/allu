import {TimeUtil} from '../../util/time.util';
import {Sort} from '../common/sort';
import {ApplicationSearchQueryForm} from './ApplicationSearchQueryForm';
import {Some} from '../../util/option';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';

export class ApplicationSearchQuery {
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
  public tags: Array<ApplicationTagType> = [];

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
    query.startTime = TimeUtil.toStartDate(queryForm.startTime);
    query.endTime = TimeUtil.toEndDate(queryForm.endTime);
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

export function fromApplicationIdAndName(applicationId: string, name: string): ApplicationSearchQuery {
  const searchQuery = new ApplicationSearchQuery();
  searchQuery.name = applicationId;
  searchQuery.applicationId = name;
  return searchQuery;
}

export function toForm(query: ApplicationSearchQuery = new ApplicationSearchQuery()): ApplicationSearchQueryForm {
  return {
    applicationId: query.applicationId,
    name: query.name,
    type: query.type,
    status: query.status,
    districts: query.districts,
    owner: query.owner,
    address: query.address,
    applicant: query.applicant,
    contact: query.contact,
    freeText: query.freeText,
    startTime: query.startTime,
    endTime: query.endTime,
    tags: query.tags
  };
}


