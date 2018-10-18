import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Invoice} from '@model/application/invoice/invoice';
import {InvoiceStatus} from '@feature/application/invoicing/invoice/invoice.component';

@Component({
  selector: 'invoice-group',
  templateUrl: './invoice-group.component.html',
  styleUrls: ['./invoice-group.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceGroupComponent {
  @Input() status: InvoiceStatus = InvoiceStatus.open;
  @Input() invoices: Invoice[] = [];
}
