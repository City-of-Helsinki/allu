
export class PostalAddress {
  constructor()
  constructor(streetAddress: string, postalCode: string, city: string)
  constructor(public streetAddress?: string, public postalCode?: string, public city?: string) {};
}
