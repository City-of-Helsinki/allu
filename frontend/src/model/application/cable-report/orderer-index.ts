import { CustomerRoleType } from '../../customer/customer-role-type';

export class OrdererIndex {
  constructor(
    public customerRoleType: string,
    public index: number
  ) {}
}
