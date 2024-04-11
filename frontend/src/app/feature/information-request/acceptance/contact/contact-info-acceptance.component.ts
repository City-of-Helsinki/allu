import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Contact} from '@model/customer/contact';
import {InfoAcceptanceDirective} from '@feature/information-request/acceptance/info-acceptance/info-acceptance.component';
import {findTranslation} from '@util/translations';
import {FormBuilder, Validators} from '@angular/forms';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';

const requiredFields = {
  name: true
};

@Component({
  selector: 'contact-info-acceptance',
  templateUrl: '../info-acceptance/info-acceptance.component.html',
  styleUrls: ['../info-acceptance/info-acceptance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContactInfoAcceptanceComponent extends InfoAcceptanceDirective<Contact> implements OnInit {
  @Output() contactChanges: EventEmitter<Contact> = new EventEmitter<Contact>();

  @Input() orderer: boolean;

  private _oldContact: Contact;
  private _newContact: Contact;

  constructor(fb: FormBuilder) {
    super(fb);
  }

  @Input() set oldContact(contact: Contact) {
    this._oldContact = contact;
    this.oldValues = this.toFieldValues(contact);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);

    // Contact id is set from old contact since we should only allow saving form when
    // contact with id is selected as reference contact
    if (contact) {
      this.form.patchValue({id: contact.id});
      this.selectAllOld();
    } else {
      this.clearSelections();
    }
  }

  @Input() set newContact(contact: Contact) {
    this._newContact = contact;
    this.newValues = this.toFieldValues(contact);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    this.fieldDescriptions = this.createDescriptions();
  }

  protected resultChanges(result: FieldValues): void {
    const contact: Contact = {...this._oldContact, orderer: this.orderer};
    contact.name = result.name;
    contact.streetAddress = result.streetAddress;
    contact.postalCode = result.postalCode;
    contact.city = result.city;
    contact.email = result.email;
    contact.phone = result.phone;
    this.contactChanges.emit(contact);
  }

  protected initResultForm(): void {
    super.initResultForm();
    const ctrl = this.fb.control(undefined, Validators.required);
    this.form.addControl('id', ctrl);
  }

  protected isRequired(field: string): boolean {
    return requiredFields[field];
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

  private createDescriptions(): FieldDescription[] {
    return [
      new FieldDescription('name', findTranslation('name')),
      new FieldDescription('streetAddress', findTranslation('postalAddress.streetAddress')),
      new FieldDescription('postalCode', findTranslation('postalAddress.postalCode')),
      new FieldDescription('city', findTranslation('postalAddress.postalOffice')),
      new FieldDescription('email', findTranslation('email')),
      new FieldDescription('phone', findTranslation('phone'))
    ];
  }
}
