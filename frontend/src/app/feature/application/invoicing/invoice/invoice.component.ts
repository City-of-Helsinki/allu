import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Invoice} from '@model/application/invoice/invoice';
import {AlluThemeColor} from '@model/common/allu-theme-color';
import {InvoiceRow} from '@model/application/invoice/invoice-row';
import {Some} from '@app/util/option';

export enum InvoiceStatus {
  open = 'open',
  pending = 'pending',
  invoiced = 'invoiced'
}

const statusToColor = {
  open: AlluThemeColor.basic,
  pending: AlluThemeColor.accent,
  invoiced: AlluThemeColor.success
};

@Component({
  selector: 'invoice',
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceComponent {
  @Input() invoice: Invoice;

  color: AlluThemeColor;

  @Input() set status(status: InvoiceStatus) {
    this.color = status ? statusToColor[status] : statusToColor.open;
  }

  get totalPrice() {
    return this.invoice ? this.invoice.rows.reduce((total: number, current: InvoiceRow) => total + current.netPrice, 0) : 0;
  }

  get invoicingDate() {
    return Some(this.invoice)
    .map(i => i.sentTime ? i.sentTime : i.invoicableTime)
    .orElse(undefined);
  }
}
