import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Invoice} from '@model/application/invoice/invoice';

@Component({
  selector: 'invoice-list',
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceListComponent {
  @Input() invoices: Invoice[];
}
