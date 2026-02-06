import {Component, Input, Output, EventEmitter} from '@angular/core';
import {Contact} from '@model/customer/contact';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {findTranslation} from '@util/translations';

@Component({
  selector: 'removed-contact-acceptance',
  templateUrl: './removed-contact-acceptance.component.html',
  styleUrls: ['./removed-contact-acceptance.component.scss']
})
export class RemovedContactAcceptanceComponent {
  @Input() contact: Contact;
  @Output() keep = new EventEmitter<number>();
  @Output() remove = new EventEmitter<number>();

  removalAccepted: boolean = null;

  get contactDescriptions(): FieldDescription[] {
    return [
      new FieldDescription('name', findTranslation('name')),
      new FieldDescription('streetAddress', findTranslation('postalAddress.streetAddress')),
      new FieldDescription('postalCode', findTranslation('postalAddress.postalCode')),
      new FieldDescription('city', findTranslation('postalAddress.postalOffice')),
      new FieldDescription('email', findTranslation('contact.email')),
      new FieldDescription('phone', findTranslation('contact.phone'))
    ];
  }

  get contactFieldValues(): FieldValues {
    if (!this.contact) { return {}; }
    return {
      name: this.contact.name,
      streetAddress: this.contact.streetAddress,
      postalCode: this.contact.postalCode,
      city: this.contact.city,
      email: this.contact.email,
      phone: this.contact.phone
    };
  }

  keepContact(): void {
    if (this.removalAccepted === false) {
      return;
    }
    this.removalAccepted = false;
    this.keep.emit(this.contact.id);
  }

  acceptRemoval(): void {
    if (this.removalAccepted === true) {
      return;
    }
    this.removalAccepted = true;
    this.remove.emit(this.contact.id);
  }
}
