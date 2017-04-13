import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {NumberUtil} from '../../util/number.util';
import {ApplicantType} from '../../model/application/applicant/applicant-type';
import {EnumUtil} from '../../util/enum.util';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {emailValidator, postalCodeValidator} from '../../util/complex-validator';
import {ApplicantForm} from '../application/info/applicant/applicant.form';
import {Contact} from '../../model/application/contact';
import {Observable, Subject} from 'rxjs';
import {CustomerHub} from '../../service/customer/customer-hub';
import {Applicant} from '../../model/application/applicant/applicant';
import {NotificationService} from '../../service/notification/notification.service';
import {findTranslation} from '../../util/translations';
import {ApplicantWithContacts} from '../../model/application/applicant/applicant-with-contacts';

@Component({
  selector: 'customer',
  template: require('./customer.component.html'),
  styles: []
})
export class CustomerComponent implements OnInit {
  applicantTypes = EnumUtil.enumValues(ApplicantType);
  customerWithContactsForm: FormGroup;
  customerForm: FormGroup;
  contactSubject = new Subject<Contact>();

  constructor(private route: ActivatedRoute,
              private router: Router,
              private customerHub: CustomerHub,
              private fb: FormBuilder) {
    this.customerForm = this.fb.group({
      id: [undefined],
      type: [undefined, Validators.required],
      name: ['', [Validators.required, Validators.minLength(2)]],
      registryKey: ['', [Validators.required, Validators.minLength(2)]],
      country: ['Suomi'],
      postalAddress: this.fb.group({
        streetAddress: [''],
        postalCode: ['', postalCodeValidator],
        city: ['']
      }),
      email: ['', emailValidator],
      phone: ['', Validators.minLength(2)],
      active: [true]
    });

    this.customerWithContactsForm = this.fb.group({
      customer: this.customerForm
    });
  }

  ngOnInit(): void {
    this.route.params
      .map(p => p['id'])
      .filter(id => NumberUtil.isDefined(id))
      .switchMap(id => this.customerHub.findApplicantById(id))
      .subscribe(customer => this.customerForm.patchValue(ApplicantForm.fromApplicant(customer)));
  }

  newContact(): void {
    this.contactSubject.next(new Contact());
  }

  removeFromRegistry(formValues: CustomerWithContactsForm): void {
    let customerId = formValues.customer.id;
    let customer = ApplicantForm.toApplicant(formValues.customer);
    customer.active = false;
    this.save(customerId, customer, this.contactChanges()).subscribe(
      applicant => this.notifyAndNavigateToCustomers(findTranslation('applicant.action.removeFromRegistry')),
      error => NotificationService.error(error)
    );
  }

  onSubmit(formValues: CustomerWithContactsForm): void {
    let customerId = formValues.customer.id;
    this.save(customerId, this.customerChanges(), this.contactChanges()).subscribe(
        applicant => this.notifyAndNavigateToCustomers(findTranslation('applicant.action.save')),
        error => NotificationService.error(error)
    );
  }

  validWithChanges(): boolean {
    return this.customerWithContactsForm.valid && this.customerWithContactsForm.dirty;
  }

  private save(customerId: number, customer: Applicant, contacts: Array<Contact>): Observable<ApplicantWithContacts> {
    return this.customerHub.saveApplicantWithContacts(customerId, customer, contacts);
  }

  private notifyAndNavigateToCustomers(message: string): void {
    NotificationService.message(message);
    this.router.navigate(['/customers']);
  }

  private customerChanges(): Applicant {
    return this.customerForm.dirty
      ? ApplicantForm.toApplicant(this.customerForm.value)
      : undefined;
  }

  private contactChanges(): Array<Contact> {
    let contacts = <FormArray>this.customerWithContactsForm.get('contacts');
    return contacts.controls
      .filter(contactCtrl => contactCtrl.dirty) // take only changed values
      .map(changed => changed.value);
  }
}

interface CustomerWithContactsForm {
  customer: ApplicantForm;
  contacts: Array<Contact>;
}
