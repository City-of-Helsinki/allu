import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable, Subscription} from 'rxjs';

import {Contact} from '../../../model/customer/contact';
import {emailValidator, postalCodeValidator} from '../../../util/complex-validator';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {NumberUtil} from '../../../util/number.util';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';

@Component({
  selector: 'customer-contacts',
  template: require('./customer-contacts.component.html'),
  styles: []
})
export class CustomerContactsComponent implements OnInit, OnDestroy {
  @Input() parentForm: FormGroup;
  @Input() onAddContact: Observable<Contact> = Observable.empty();

  contacts: FormArray;

  private contactSubscription: Subscription;

  constructor(private route: ActivatedRoute, private fb: FormBuilder, private customerHub: CustomerHub) {}

  ngOnInit(): void {
    this.contacts = <FormArray>this.parentForm.get('contacts');
    this.contactSubscription = this.onAddContact.subscribe(c => this.addContact(c));

    this.route.params
      .map(p => p['id'])
      .filter(id => NumberUtil.isDefined(id))
      .switchMap(id => this.customerHub.findCustomerActiveContacts(id))
      .subscribe(contacts => contacts.forEach(c => this.addContact(c)));
  }

  ngOnDestroy(): void {
    this.contactSubscription.unsubscribe();
  }

  private addContact(contact: Contact): void {
    this.contacts.push(this.createContact(contact));
  }

  private removeContact(index: number, contact: Contact): void {
    if (NumberUtil.isDefined(contact.id)) {
      let contactCtrl = this.contacts.at(index);
      contactCtrl.patchValue({active: false});
      contactCtrl.markAsDirty();
    } else {
      this.contacts.removeAt(index);
    }
    NotificationService.message(findTranslation('contact.action.remove'));
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
