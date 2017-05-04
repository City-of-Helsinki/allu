import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {MdDialog, MdDialogRef} from '@angular/material';
import {InvoiceRow} from '../../../model/application/invoice/invoice-row';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';
import {InvoiceRowForm} from './invoice-row.form';
import {InvoiceRowModalComponent} from './invoice-row-modal.component';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from 'rxjs/Observable';
import {InvoiceUnit} from '../../../model/application/invoice/invoice-unit';


@Component({
  selector: 'invoice-rows',
  template: require('./invoice-rows.component.html'),
  styles: [
    require('./invoice-rows.component.scss')
  ]
})
export class InvoiceRowsComponent implements OnInit, OnDestroy {

  @Input() parentForm: FormGroup;
  @Input() applicationId: number;
  pendingInvoiceRows: FormArray;
  acceptedInvoiceRows: FormArray;

  private rowSubscription = new Subscription();
  private dialogRef: MdDialogRef<InvoiceRowModalComponent>;


  constructor(private fb: FormBuilder, private dialog: MdDialog, private invoiceHub: InvoiceHub) {
    this.pendingInvoiceRows = fb.array([]);
    this.acceptedInvoiceRows = fb.array([]);
  }

  ngOnInit(): void {
    this.parentForm.addControl('pendingInvoiceRows', this.pendingInvoiceRows);
    this.parentForm.addControl('acceptedInvoiceRows', this.acceptedInvoiceRows);
    this.invoiceHub.loadInvoiceRows(this.applicationId)
      .subscribe(rows => {}, error => NotificationService.error(error));

    this.rowSubscription = this.invoiceHub.invoiceRows
      .subscribe(rows => this.rowsUpdated(rows));
  }

  ngOnDestroy(): void {
    this.rowSubscription.unsubscribe();
  }

  newRow(): void {
    this.openModal(new InvoiceRow(InvoiceUnit.DAY)).subscribe(row => {
      this.addPendingRow(row);
      this.parentForm.markAsDirty();
    });
  }

  editRow(index: number): void {
    let row = InvoiceRowForm.toInvoiceRow(this.pendingInvoiceRows.at(index).value);
    this.openModal(row)
      .subscribe(updatedRow => {
        this.updatePendingRow(updatedRow, index);
        this.parentForm.markAsDirty();
      });
  }

  acceptRow(index: number): void {
    let row = this.pendingInvoiceRows.at(index);
    this.pendingInvoiceRows.removeAt(index);
    NotificationService.message(findTranslation('invoice.row.action.accepted'));
    this.acceptedInvoiceRows.push(row);
  }

  private rowsUpdated(rows: Array<InvoiceRow>): void {
    this.pendingInvoiceRows = this.fb.array([]);
    this.acceptedInvoiceRows = this.fb.array([]);
    this.parentForm.setControl('pendingInvoiceRows', this.pendingInvoiceRows);
    this.parentForm.setControl('acceptedInvoiceRows', this.acceptedInvoiceRows);
    rows.forEach(row => this.addPendingRow(row));
  }

  private addPendingRow(row: InvoiceRow): void {
    this.pendingInvoiceRows.push(InvoiceRowForm.formGroup(this.fb, row));
  }

  private updatePendingRow(row: InvoiceRow, index: number): void {
    this.pendingInvoiceRows.at(index).patchValue(InvoiceRowForm.toFormValue(row));
  }

  private openModal(row?: InvoiceRow): Observable<InvoiceRow> {
    this.dialogRef = this.dialog.open(InvoiceRowModalComponent, {width: '600px', data: {invoiceRow: row}});
    this.dialogRef.componentInstance.invoiceRow = row;
    return this.dialogRef.afterClosed()
      .filter(r => !!r);
  }
}
