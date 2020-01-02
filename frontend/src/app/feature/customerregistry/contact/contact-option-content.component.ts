import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Contact} from '@model/customer/contact';

@Component({
  selector: 'contact-option-content',
  templateUrl: './contact-option-content.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContactOptionContentComponent {
  @Input() contact: Contact;

  constructor() {}
}
