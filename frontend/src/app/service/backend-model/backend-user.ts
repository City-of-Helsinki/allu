export interface BackendUser {
  id: number;
  userName: string;
  realName: string;
  emailAddress: string;
  phone: string;
  title: string;
  active: boolean;
  lastLogin: string;
  allowedApplicationTypes: Array<string>;
  assignedRoles: Array<string>;
  cityDistrictIds: Array<number>;
}

export interface SearchResultUser {
  userName: string;
  realName: string;
}
