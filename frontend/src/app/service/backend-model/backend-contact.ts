
export interface BackendContact {
  id: number;
  customerId: number;
  name: string;
  streetAddress: string;
  postalCode: string;
  city: string;
  email: string;
  phone: string;
  active: boolean;
  orderer?: boolean;
}
