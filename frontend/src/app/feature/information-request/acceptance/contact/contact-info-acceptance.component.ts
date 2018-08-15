import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Contact} from '@model/customer/contact';
import {InfoAcceptance} from '@feature/information-request/acceptance/info-acceptance';
import {FieldLabels, FieldValues} from '@feature/information-request/acceptance/field-group-acceptance.component';
import {findTranslation} from '@util/translations';

@Component({
  selector: 'contact-info-acceptance',
  templateUrl: './contact-info-acceptance.component.html',
  styleUrls: []
})
export class ContactInfoAcceptanceComponent extends InfoAcceptance<Contact> implements OnInit {
  @Output() contactChanges: EventEmitter<Contact> = new EventEmitter<Contact>();

  @Input() readonly: boolean;

  private _oldContact: Contact;
  private _newContact: Contact;

  @Input() set oldContact(contact: Contact) {
    this._oldContact = contact;
    this.oldValues = this.toFieldValues(contact);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);
  }

  @Input() set newContact(contact: Contact) {
    this._newContact = contact;
    this.newValues = this.toFieldValues(contact);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    this.fieldLabels = this.createLabels();
  }

  protected resultChanges(result: FieldValues): void {
    const contact = {...this._oldContact};
    contact.name = result.name;
    contact.streetAddress = result.streetAddress;
    contact.postalCode = result.postalCode;
    contact.city = result.city;
    contact.email = result.email;
    contact.phone = result.phone;
    this.contactChanges.emit(contact);
  }

  private toFieldValues(contact: Contact): FieldValues {
    if (contact) {
      return {
        name: contact.name,
        streetAddress: contact.streetAddress,
        postalCode: contact.postalCode,
        city: contact.city,
        email: contact.email,
        phone: contact.phone
      };
    } else {
      return {};
    }
  }

  private toDisplayValues(fieldValues: FieldValues): FieldValues {
    return {...fieldValues};
  }

  private createLabels(): FieldLabels {
    return {
      name: findTranslation('name'),
      streetAddress: findTranslation('postalAddress.streetAddress'),
      postalCode: findTranslation('postalAddress.postalCode'),
      city: findTranslation('postalAddress.postalOffice'),
      email: findTranslation('email'),
      phone: findTranslation('phone'),
    };
  }
}
