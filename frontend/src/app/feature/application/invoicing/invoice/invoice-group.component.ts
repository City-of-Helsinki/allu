import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Invoice} from '@model/application/invoice/invoice';

@Component({
  selector: 'invoice-group',
  templateUrl: './invoice-group.component.html',
  styleUrls: ['./invoice-group.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceGroupComponent {
  @Input() title: string;
  @Input() invoices: Invoice[] = [];
}
