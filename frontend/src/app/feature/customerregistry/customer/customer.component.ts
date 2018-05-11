import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {NumberUtil} from '../../../util/number.util';
import {CustomerType} from '../../../model/customer/customer-type';
import {EnumUtil} from '../../../util/enum.util';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {CustomerForm} from './customer.form';
import {Contact} from '../../../model/customer/contact';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Customer} from '../../../model/customer/customer';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {CustomerWithContactsForm} from './customer-with-contacts.form';
import {CustomerService} from '../../../service/customer/customer.service';

@Component({
  selector: 'customer',
  templateUrl: './customer.component.html',
  styleUrls: [
    './customer.component.scss'
  ]
})
export class CustomerComponent implements OnInit {
  customerTypes = EnumUtil.enumValues(CustomerType);
  form: FormGroup;
  customerForm: FormGroup;
  contactSubject = new Subject<Contact>();

  constructor(private route: ActivatedRoute,
              private router: Router,
              private customerService: CustomerService,
              private fb: FormBuilder,
              private notification: NotificationService) {
    this.form = CustomerWithContactsForm.initialForm(this.fb);
    this.customerForm = <FormGroup>this.form.get('customer');
  }

  ngOnInit(): void {
    this.route.params
      .map(p => p['id'])
      .filter(id => NumberUtil.isDefined(id))
      .switchMap(id => this.customerService.findCustomerById(id))
      .subscribe(customer => this.customerForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  newContact(): void {
    this.contactSubject.next(new Contact());
  }

  removeFromRegistry(formValues: CustomerWithContactsForm): void {
    const customer = CustomerForm.toCustomer(formValues.customer);
    customer.active = false;
    this.save(customer, this.contactChanges()).subscribe(
      c => this.notifyAndNavigateToCustomers(findTranslation('customer.action.removeFromRegistry')),
      error => this.notification.errorInfo(error)
    );
  }

  onSubmit(formValues: CustomerWithContactsForm): void {
    this.save(this.customerChanges(), this.contactChanges()).subscribe(
        customer => this.notifyAndNavigateToCustomers(findTranslation('customer.action.save')),
        error => this.notification.errorInfo(error)
    );
  }

  validWithChanges(): boolean {
    return this.form.valid && this.form.dirty;
  }

  private save(customer: Customer, contacts: Array<Contact>): Observable<CustomerWithContacts> {
    const customerWithContacts = new CustomerWithContacts(undefined, customer, contacts);
    return this.customerService.saveCustomerWithContacts(customerWithContacts);
  }

  private notifyAndNavigateToCustomers(message: string): void {
    this.notification.success(message);
    this.router.navigate(['/customers']);
  }

  private customerChanges(): Customer {
    return CustomerForm.toCustomer(this.customerForm.value);
  }

  private contactChanges(): Array<Contact> {
    const contacts = <FormArray>this.form.get('contacts');
    return contacts.controls
      .filter(contactCtrl => contactCtrl.dirty) // take only changed values
      .map(changed => changed.value);
  }
}
