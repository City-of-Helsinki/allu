import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Customer} from '@model/customer/customer';

@Component({
  selector: 'customer-option-content',
  templateUrl: './customer-option-content.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerOptionContentComponent {
  @Input() customer: Customer;
  @Input() showRegistryKey = false;
  @Input() showInvoicingInfo = false;

  constructor() {}
}
