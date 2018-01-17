export interface ApplicationSearchQueryForm {
  applicationId?: string;
  type?: Array<string>;
  status?: Array<string>;
  owner?: Array<string>;
  address?: string;
  districts?: Array<number>;
  applicant?: string;
  contact?: string;
  freeText?: string;
  startTime?: Date;
  endTime?: Date;
  tags?: Array<string>;
}
