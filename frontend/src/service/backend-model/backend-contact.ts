
export interface BackendContact {
  id: number;
  applicantId: number;
  name: string;
  streetAddress: string;
  postalCode: string;
  city: string;
  email: string;
  phone: string;
  active: boolean;
}
