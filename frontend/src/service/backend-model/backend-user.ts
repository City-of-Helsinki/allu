export interface BackendUser {
  id: number;
  userName: string;
  realName: string;
  emailAddress: string;
  title: string;
  active: boolean;
  allowedApplicationTypes: Array<string>;
  assignedRoles: Array<string>;
  cityDistrictIds: Array<number>;
}
