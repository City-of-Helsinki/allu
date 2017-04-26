import {Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {InvoiceRow} from '../../../model/application/invoice/invoice-row';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';

@Component({
  selector: 'invoice-rows',
  template: require('./invoice-rows.component.html'),
  styles: [
    require('./invoice-rows.component.scss')
  ]
})
export class InvoiceRowsComponent implements OnInit {

  @Input() parentForm: FormGroup;
  @Input() applicationId: number;
  invoiceRows: FormArray;


  constructor(private fb: FormBuilder, private invoiceHub: InvoiceHub) {
    this.invoiceRows = fb.array([]);
  }

  ngOnInit(): void {
    this.parentForm.addControl('invoiceRows', this.invoiceRows);
    this.invoiceHub.getInvoiceRows(this.applicationId)
      .subscribe(rows => rows.forEach(row => this.addRow(row)));

  }

  private addRow(row: InvoiceRow): void {
    this.invoiceRows.push(InvoiceRow.formGroup(this.fb, row));
  }
}
