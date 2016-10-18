export interface ApplicationSearchQueryForm {
  applicationId?: string;
  type?: Array<string>;
  status?: Array<string>;
  handler?: Array<string>;
  address?: string;
  district?: string;
  applicant?: string;
  contact?: string;
  freeText?: string;
  startTime?: string;
  endTime?: string;
}
