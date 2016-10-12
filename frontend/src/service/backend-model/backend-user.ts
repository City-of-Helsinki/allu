export interface BackendUser {
  id: number;
  userName: string;
  realName: string;
  emailAddress: string;
  title: string;
  isActive: boolean;
  allowedApplicationTypes: Array<string>;
  assignedRoles: Array<string>;
}
