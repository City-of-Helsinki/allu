import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Invoice} from '@model/application/invoice/invoice';

@Component({
  selector: 'invoice-list',
  templateUrl: './invoice-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceListComponent {
  open: Invoice[] = [];
  pending: Invoice[] = [];
  invoiced: Invoice[] = [];

  @Input() set invoices(invoices: Invoice[]) {
    invoices = invoices || [];
    this.open = invoices.filter(i => i.invoicableTime === undefined);
    this.pending = invoices.filter(i => i.invoicableTime !== undefined && i.invoiced === false);
    this.invoiced = invoices.filter(i => i.invoiced === true);
  }
}
