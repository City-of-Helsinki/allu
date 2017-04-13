export class Contact {
  constructor(
    public id?: number,
    public applicantId?: number,
    public name?: string,
    public streetAddress?: string,
    public postalCode?: string,
    public city?: string,
    public email?: string,
    public phone?: string,
    public active = true) {}
}
