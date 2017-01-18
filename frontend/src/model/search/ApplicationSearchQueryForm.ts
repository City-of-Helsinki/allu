export interface ApplicationSearchQueryForm {
  applicationId?: string;
  type?: Array<string>;
  status?: Array<string>;
  handler?: Array<string>;
  address?: string;
  districts?: Array<number>;
  applicant?: string;
  contact?: string;
  freeText?: string;
  startTime?: string;
  endTime?: string;
  tags?: Array<string>;
}
