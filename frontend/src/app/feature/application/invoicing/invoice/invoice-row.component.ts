import {ChangeDetectionStrategy, Component, HostBinding, Input} from '@angular/core';
import {InvoiceRow} from '@model/application/invoice/invoice-row';

@Component({
  selector: 'invoice-row',
  templateUrl: './invoice-row.component.html',
  styleUrls: ['./invoice-row.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceRowComponent {
  @Input() row: InvoiceRow;

  @HostBinding('class') classNames = 'invoice-row';
}
