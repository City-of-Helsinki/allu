export interface BackendProject {
  id: number;
  name: string;
  startTime: string;
  endTime: string;
  cityDistricts: Array<number>;
  ownerName: string;
  contactName: string;
  email: string;
  phone: string;
  customerReference: string;
  additionalInfo: string;
  parentId: number;
}
