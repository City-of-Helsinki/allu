import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {NumberUtil} from '../../../util/number.util';
import {CustomerType} from '../../../model/customer/customer-type';
import {EnumUtil} from '../../../util/enum.util';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {CustomerForm} from './customer.form';
import {Contact} from '../../../model/customer/contact';
import {Observable, Subject} from 'rxjs';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Customer} from '../../../model/customer/customer';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {CustomerWithContactsForm} from './customer-with-contacts.form';

@Component({
  selector: 'customer',
  template: require('./customer.component.html'),
  styles: [
    require('./customer.component.scss')
  ]
})
export class CustomerComponent implements OnInit {
  customerTypes = EnumUtil.enumValues(CustomerType);
  form: FormGroup;
  customerForm: FormGroup;
  contactSubject = new Subject<Contact>();

  constructor(private route: ActivatedRoute,
              private router: Router,
              private customerHub: CustomerHub,
              private fb: FormBuilder) {
    this.form = CustomerWithContactsForm.initialForm(this.fb);
    this.customerForm = <FormGroup>this.form.get('customer');
  }

  ngOnInit(): void {
    this.route.params
      .map(p => p['id'])
      .filter(id => NumberUtil.isDefined(id))
      .switchMap(id => this.customerHub.findCustomerById(id))
      .subscribe(customer => this.customerForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  newContact(): void {
    this.contactSubject.next(new Contact());
  }

  removeFromRegistry(formValues: CustomerWithContactsForm): void {
    let customer = CustomerForm.toCustomer(formValues.customer);
    customer.active = false;
    this.save(customer, this.contactChanges()).subscribe(
      c => this.notifyAndNavigateToCustomers(findTranslation('customer.action.removeFromRegistry')),
      error => NotificationService.error(error)
    );
  }

  onSubmit(formValues: CustomerWithContactsForm): void {
    this.save(this.customerChanges(), this.contactChanges()).subscribe(
        customer => this.notifyAndNavigateToCustomers(findTranslation('customer.action.save')),
        error => NotificationService.error(error)
    );
  }

  validWithChanges(): boolean {
    return this.form.valid && this.form.dirty;
  }

  private save(customer: Customer, contacts: Array<Contact>): Observable<CustomerWithContacts> {
    const customerWithContacts = new CustomerWithContacts(undefined, customer, contacts);
    return this.customerHub.saveCustomerWithContacts(customerWithContacts);
  }

  private notifyAndNavigateToCustomers(message: string): void {
    NotificationService.message(message);
    this.router.navigate(['/customers']);
  }

  private customerChanges(): Customer {
    return CustomerForm.toCustomer(this.customerForm.value);
  }

  private contactChanges(): Array<Contact> {
    let contacts = <FormArray>this.form.get('contacts');
    return contacts.controls
      .filter(contactCtrl => contactCtrl.dirty) // take only changed values
      .map(changed => changed.value);
  }
}
