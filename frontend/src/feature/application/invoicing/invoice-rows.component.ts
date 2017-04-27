import {Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {MdDialog, MdDialogRef} from '@angular/material';
import {InvoiceRow} from '../../../model/application/invoice/invoice-row';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';
import {InvoiceRowForm} from './invoice-row.form';
import {InvoiceRowModalComponent} from './invoice-row-modal.component';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';


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
  pendingInvoiceRows: FormArray;
  acceptedInvoiceRows: FormArray;

  private dialogRef: MdDialogRef<InvoiceRowModalComponent>;


  constructor(private fb: FormBuilder, private dialog: MdDialog, private invoiceHub: InvoiceHub) {
    this.pendingInvoiceRows = fb.array([]);
    this.acceptedInvoiceRows = fb.array([]);
  }

  ngOnInit(): void {
    this.parentForm.addControl('pendingInvoiceRows', this.pendingInvoiceRows);
    this.parentForm.addControl('acceptedInvoiceRows', this.acceptedInvoiceRows);
    this.invoiceHub.getInvoiceRows(this.applicationId)
      .subscribe(rows => rows.forEach(row => this.addPendingRow(row)));

  }

  newRow(): void {
    this.dialogRef = this.dialog.open(InvoiceRowModalComponent, {width: '600px'});
    this.dialogRef.afterClosed()
      .filter(row => !!row)
      .subscribe(row => this.addPendingRow(row));
  }

  acceptRow(index: number): void {
    let row = this.pendingInvoiceRows.at(index);
    this.pendingInvoiceRows.removeAt(index);
    // TODO: Save here before pushing or save as whole?
    NotificationService.message(findTranslation('invoice.row.action.accepted'));
    this.acceptedInvoiceRows.push(row);
  }

  private addPendingRow(row: InvoiceRow): void {
    this.pendingInvoiceRows.push(InvoiceRowForm.formGroup(this.fb, row));
  }
}
