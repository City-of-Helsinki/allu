import {Applicant} from '../../../../model/application/applicant/applicant';
import {PostalAddress} from '../../../../model/common/postal-address';
import {FormBuilder, Validators} from '@angular/forms';
import {emailValidator, postalCodeValidator} from '../../../../util/complex-validator';

export class ApplicantForm {

  constructor(
    public id?: number,
    public type?: string,
    public representative?: boolean,
    public name?: string,
    public registryKey?: string,
    public country?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public active = true
  ) {}

  static fromApplicant(applicant: Applicant): ApplicantForm {
    return new ApplicantForm(
      applicant.id,
      applicant.type,
      applicant.representative,
      applicant.name,
      applicant.registryKey,
      'Suomi',
      applicant.postalAddress,
      applicant.email,
      applicant.phone,
      applicant.active
    );
  }

  static toApplicant(form: ApplicantForm): Applicant {
    let applicant = new Applicant();
    applicant.id = form.id;
    applicant.type = form.type;
    applicant.representative = form.representative;
    applicant.name = form.name;
    applicant.registryKey = form.registryKey;
    applicant.postalAddress = form.postalAddress;
    applicant.email = form.email;
    applicant.phone = form.phone;
    applicant.active = form.active;
    return applicant;
  }

  static initialForm(fb: FormBuilder): any {
    return fb.group({
      id: undefined,
      type: [undefined, Validators.required],
      representative: [undefined],
      detailsId: undefined,
      name: ['', [Validators.required, Validators.minLength(2)]],
      registryKey: ['', [Validators.required, Validators.minLength(2)]],
      country: ['Suomi'],
      postalAddress: fb.group({
        streetAddress: [''],
        postalCode: ['', postalCodeValidator],
        city: ['']
      }),
      email: ['', emailValidator],
      phone: ['', Validators.minLength(2)],
      propertyDeveloper: [false],
      active: [true]
    });
  }
}
