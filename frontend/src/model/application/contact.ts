export class Contact {
  constructor(
    public id: number,
    public organizationId: number,
    public name: string,
    public streetAddress: string,
    public postalCode: string,
    public city: string,
    public email: string,
    public phone: string) {}
}
