import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {emailValidator, postalCodeValidator} from '../../util/complex-validator';
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

  static formGroup(fb: FormBuilder, contact: Contact = new Contact()): FormGroup {
    return fb.group({
      id: contact.id,
      applicantId: contact.applicantId,
      name: [contact.name, [Validators.required, Validators.minLength(2)]],
      streetAddress: [contact.streetAddress],
      postalCode: [contact.postalCode, postalCodeValidator],
      city: [contact.city],
      email: [contact.email, emailValidator],
      phone: [contact.phone, Validators.minLength(2)],
      active: [contact.active]
    });
  }
}
