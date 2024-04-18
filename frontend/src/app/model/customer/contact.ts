import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {postalCodeValidator} from '@util/complex-validator';

export class Contact {
  constructor(
    public id?: number,
    public customerId?: number,
    public name?: string,
    public streetAddress?: string,
    public postalCode?: string,
    public city?: string,
    public email?: string,
    public phone?: string,
    public active = true,
    public orderer?: boolean) {}

  static formGroup(fb: UntypedFormBuilder, contact: Contact = new Contact()): UntypedFormGroup {
    return fb.group({
      id: contact.id,
      customerId: contact.customerId,
      name: [contact.name, [Validators.required, Validators.minLength(2)]],
      streetAddress: [contact.streetAddress],
      postalCode: [contact.postalCode, postalCodeValidator],
      city: [contact.city],
      email: [contact.email, Validators.email],
      phone: [contact.phone, Validators.minLength(2)],
      active: [contact.active]
    });
  }
}
