import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Invoice} from '@model/application/invoice/invoice';

@Component({
  selector: 'invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceComponent {
  @Input() invoice: Invoice;
}
