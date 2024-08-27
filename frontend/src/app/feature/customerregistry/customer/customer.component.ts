import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable, Subject} from 'rxjs';
import {NumberUtil} from '../../../util/number.util';
import {CustomerType} from '../../../model/customer/customer-type';
import {EnumUtil} from '../../../util/enum.util';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {CustomerForm} from './customer.form';
import {Contact} from '../../../model/customer/contact';
import {NotificationService} from '../../notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Customer} from '../../../model/customer/customer';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {CustomerWithContactsForm} from './customer-with-contacts.form';
import {CustomerService} from '../../../service/customer/customer.service';
import {filter, map, switchMap} from 'rxjs/internal/operators';
import {FormUtil} from '@util/form.util';
import {createTranslated} from '@service/error/error-info';
import { CurrentUser } from '@app/service/user/current-user';
import { RoleType } from '@app/model/user/role-type';


@Component({
  selector: 'customer',
  templateUrl: './customer.component.html',
  styleUrls: [
    './customer.component.scss'
  ]
})
export class CustomerComponent implements OnInit {
  customerTypes = EnumUtil.enumValues(CustomerType);
  form: UntypedFormGroup;
  customerForm: UntypedFormGroup;
  contactSubject = new Subject<Contact>();
  isRemoveVisible = false;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private customerService: CustomerService,
              private fb: UntypedFormBuilder,
              private notification: NotificationService,
              private currentUser: CurrentUser) {
    this.form = CustomerWithContactsForm.initialForm(this.fb);
    this.customerForm = <UntypedFormGroup>this.form.get('customer');
  }

  ngOnInit(): void {
    this.route.params.pipe(
      map(p => p['id']),
      filter(id => NumberUtil.isDefined(id)),
      switchMap(id => this.customerService.findCustomerById(id))
    ).subscribe(customer => this.customerForm.patchValue(CustomerForm.fromCustomer(customer)));

    this.removeButtonVisibilityStatus();
  }

  async removeButtonVisibilityStatus(): Promise<void> {
    // ALLU-19 restrict usage to admin and invoicing roles when sap number exists, this hides the remove
    const userHasRole = await this.currentUser.hasRole([RoleType.ROLE_INVOICING, RoleType.ROLE_ADMIN].map(role => RoleType[role])).toPromise();

    if (this.customerForm.value.sapCustomerNumber && userHasRole) {
      this.isRemoveVisible = true;
    } else {
      if (this.customerForm.value.id) this.isRemoveVisible = true;
    }
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
    if (this.form.valid && this.form.dirty) {
      this.save(this.customerChanges(), this.contactChanges()).subscribe(
        customer => this.notifyAndNavigateToCustomers(findTranslation('customer.action.save')),
        error => this.notification.errorInfo(error)
      );
    } else {
      FormUtil.validateFormFields(this.form);
      this.notification.errorInfo(createTranslated('common.field.faultyValueTitle', 'common.field.faultyValue'));
    }
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
    const contacts = <UntypedFormArray>this.form.get('contacts');
    return contacts.controls
      .filter(contactCtrl => contactCtrl.dirty) // take only changed values
      .map(changed => changed.value);
  }
}
