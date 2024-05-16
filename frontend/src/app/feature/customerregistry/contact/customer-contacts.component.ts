import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

import {Contact} from '../../../model/customer/contact';
import {postalCodeValidator} from '../../../util/complex-validator';
import {NumberUtil} from '../../../util/number.util';
import {EMPTY, Observable, Subscription} from 'rxjs';
import {NotificationService} from '../../notification/notification.service';
import {CustomerService} from '../../../service/customer/customer.service';
import {filter, map, switchMap} from 'rxjs/internal/operators';

@Component({
  selector: 'customer-contacts',
  templateUrl: './customer-contacts.component.html',
  styleUrls: []
})
export class CustomerContactsComponent implements OnInit, OnDestroy {
  @Input() parentForm: UntypedFormGroup;
  @Input() onAddContact: Observable<Contact> = EMPTY;

  contacts: UntypedFormArray;

  private contactSubscription: Subscription;
  private customerId: number;

  constructor(private route: ActivatedRoute,
              private fb: UntypedFormBuilder,
              private customerService: CustomerService,
              private notification: NotificationService) {}

  ngOnInit(): void {
    this.contacts = <UntypedFormArray>this.parentForm.get('contacts');
    this.contactSubscription = this.onAddContact.subscribe(c => this.addContact(c));
    this.route.params.pipe(map(p => p['id'])).subscribe(p => this.customerId = p);

    this.route.params.pipe(
      map(p => p['id']),
      filter(id => NumberUtil.isDefined(id)),
      switchMap(id => this.customerService.findCustomerActiveContacts(id))
    ).subscribe(contacts => contacts.forEach(c => this.addContact(c)));
  }

  ngOnDestroy(): void {
    this.contactSubscription.unsubscribe();
  }

  removeContact(index: number, contactValue: any): void {
    if (NumberUtil.isDefined(contactValue.id)) {
      this.contacts.at(index).patchValue({active: false});
      this.customerService.saveContactsForCustomer(this.customerId, this.contacts.value)
        .subscribe(
          result => this.notification.translateSuccess('customers.notifications.contactRemoved'),
          error => this.notification.translateErrorMessage('customers.notifications.contactRemoveFailed'));
    } else {
      this.contacts.removeAt(index);
    }
  }

  private addContact(contact: Contact): void {
    this.contacts.push(this.createContact(contact));
  }

  private createContact(contact: Contact): UntypedFormGroup {
    return this.fb.group({
      id: [contact.id],
      customerId: [contact.customerId],
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
