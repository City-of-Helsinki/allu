export interface MapFeatureInfo {
  id: number;
  applicationId?: string;
  type?: string;
  name?: string;
  startTime: Date;
  endTime: Date;
  recurringEndTime?: Date;
  terminationTime?: Date;
  applicant?: string;
}
