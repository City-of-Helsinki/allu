export interface BackendExternalUser {
  id: number;
  username: string;
  name: string;
  emailAddress: string;
  active: boolean;
  lastLogin: string;
  assignedRoles: Array<string>;
  token: string;
  expirationTime: string;
  connectedCustomers?: Array<number>;
}
