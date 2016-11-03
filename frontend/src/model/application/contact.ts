export class Contact {
  constructor();
  constructor(
    id: number,
    organizationId: number,
    name: string,
    streetAddress: string,
    postalCode: string,
    city: string,
    email: string,
    phone: string
  );
  constructor(
    public id?: number,
    public organizationId?: number,
    public name?: string,
    public streetAddress?: string,
    public postalCode?: string,
    public city?: string,
    public email?: string,
    public phone?: string) {}
}
