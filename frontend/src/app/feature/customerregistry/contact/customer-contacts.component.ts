import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

import {Contact} from '../../../model/customer/contact';
import {emailValidator, postalCodeValidator} from '../../../util/complex-validator';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {NumberUtil} from '../../../util/number.util';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from 'rxjs/Observable';
import {NotificationService} from '../../../service/notification/notification.service';

@Component({
  selector: 'customer-contacts',
  templateUrl: './customer-contacts.component.html',
  styleUrls: []
})
export class CustomerContactsComponent implements OnInit, OnDestroy {
  @Input() parentForm: FormGroup;
  @Input() onAddContact: Observable<Contact> = Observable.empty();

  contacts: FormArray;

  private contactSubscription: Subscription;
  private customerId: number;

  constructor(private route: ActivatedRoute, private fb: FormBuilder, private customerHub: CustomerHub) {}

  ngOnInit(): void {
    this.contacts = <FormArray>this.parentForm.get('contacts');
    this.contactSubscription = this.onAddContact.subscribe(c => this.addContact(c));
    this.route.params.map(p => p['id']).subscribe(p => this.customerId = p);

    this.route.params
      .map(p => p['id'])
      .filter(id => NumberUtil.isDefined(id))
      .switchMap(id => this.customerHub.findCustomerActiveContacts(id))
      .subscribe(contacts => contacts.forEach(c => this.addContact(c)));
  }

  ngOnDestroy(): void {
    this.contactSubscription.unsubscribe();
  }

  removeContact(index: number, contactValue: any): void {
    if (NumberUtil.isDefined(contactValue.id)) {
      this.contacts.at(index).patchValue({active: false});
      this.customerHub.saveContactsForCustomer(this.customerId, this.contacts.value)
        .subscribe(
          result => NotificationService.translateMessage('customers.notifications.contactRemoved'),
          error => NotificationService.translateErrorMessage('customers.notifications.contactRemoveFailed'));
    } else {
      this.contacts.removeAt(index);
    }
  }

  private addContact(contact: Contact): void {
    this.contacts.push(this.createContact(contact));
  }

  private createContact(contact: Contact): FormGroup {
    return this.fb.group({
      id: [contact.id],
      customerId: [contact.customerId],
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
