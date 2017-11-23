import {BackendPostalAddress} from '../../service/backend-model/backend-postal-address';

export class PostalAddress {
  constructor(public streetAddress?: string, public postalCode?: string, public city?: string) {}

  public static fromBackend(backendPostalAddress: BackendPostalAddress): PostalAddress {
    return new PostalAddress(
      backendPostalAddress.streetAddress,
      backendPostalAddress.postalCode,
      backendPostalAddress.city);
  }

  public toBackend(): BackendPostalAddress {
    return {
      streetAddress: this.streetAddress,
      postalCode: this.postalCode,
      city: this.city
    };
  }

  get uiStreetAddress(): string {
    return this.streetAddress ? this.streetAddress.replace(/\b0+/g, '') : undefined;
  }

  get uiAddress(): string {
    let uiAddress = '';
    uiAddress += this.streetAddress ? this.streetAddress : '';
    uiAddress += this.postalCode ? ' ' + this.postalCode : '';
    uiAddress += this.city ? ' ' + this.city : '';
    return uiAddress;
  }
}
