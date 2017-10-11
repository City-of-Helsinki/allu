import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ChargeBasisEntry} from '../../../model/application/invoice/charge-basis-entry';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';
import {ChargeBasisEntryForm} from './charge-basis-entry.form';
import {ChargeBasisEntryModalComponent} from './charge-basis-entry-modal.component';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from 'rxjs/Observable';
import {ChargeBasisUnit} from '../../../model/application/invoice/charge-basis-unit';


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
  private dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>;


  constructor(private fb: FormBuilder, private dialog: MatDialog, private invoiceHub: InvoiceHub) {
    this.pendingInvoiceRows = fb.array([]);
    this.acceptedInvoiceRows = fb.array([]);
  }

  ngOnInit(): void {
    this.parentForm.addControl('pendingInvoiceRows', this.pendingInvoiceRows);
    this.parentForm.addControl('acceptedInvoiceRows', this.acceptedInvoiceRows);
    this.invoiceHub.loadInvoiceRows(this.applicationId)
      .subscribe(entries => {}, error => NotificationService.error(error));

    this.rowSubscription = this.invoiceHub.invoiceRows
      .subscribe(entries => this.entriesUpdated(entries));
  }

  ngOnDestroy(): void {
    this.rowSubscription.unsubscribe();
  }

  newRow(): void {
    this.openModal(new ChargeBasisEntry(ChargeBasisUnit.DAY)).subscribe(entry => {
      this.addPendingRow(entry);
      this.parentForm.markAsDirty();
    });
  }

  editRow(index: number): void {
    let entry = ChargeBasisEntryForm.toChargeBasisEntry(this.pendingInvoiceRows.at(index).value);
    this.openModal(entry)
      .subscribe(updatedEntry => {
        this.updatePendingRow(updatedEntry, index);
        this.parentForm.markAsDirty();
      });
  }

  acceptRow(index: number): void {
    let row = this.pendingInvoiceRows.at(index);
    this.pendingInvoiceRows.removeAt(index);
    NotificationService.message(findTranslation('invoice.row.action.accepted'));
    this.acceptedInvoiceRows.push(row);
  }

  private entriesUpdated(entries: Array<ChargeBasisEntry>): void {
    this.pendingInvoiceRows = this.fb.array([]);
    this.acceptedInvoiceRows = this.fb.array([]);
    this.parentForm.setControl('pendingInvoiceRows', this.pendingInvoiceRows);
    this.parentForm.setControl('acceptedInvoiceRows', this.acceptedInvoiceRows);
    entries.forEach(entry => this.addPendingRow(entry));
  }

  private addPendingRow(entry: ChargeBasisEntry): void {
    this.pendingInvoiceRows.push(ChargeBasisEntryForm.formGroup(this.fb, entry));
  }

  private updatePendingRow(entry: ChargeBasisEntry, index: number): void {
    this.pendingInvoiceRows.at(index).patchValue(ChargeBasisEntryForm.toFormValue(entry));
  }

  private openModal(entry?: ChargeBasisEntry): Observable<ChargeBasisEntry> {
    this.dialogRef = this.dialog.open<ChargeBasisEntryModalComponent>(ChargeBasisEntryModalComponent, {
      width: '600px',
      data: {invoiceRow: entry}
    });
    this.dialogRef.componentInstance.chargeBasisEntry = entry;
    return this.dialogRef.afterClosed()
      .filter(r => !!r);
  }
}
