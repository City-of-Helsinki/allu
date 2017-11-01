import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ChargeBasisEntry} from '../../../../model/application/invoice/charge-basis-entry';
import {InvoiceHub} from '../../../../service/application/invoice/invoice-hub';
import {ChargeBasisEntryForm} from './charge-basis-entry.form';
import {CHARGE_BASIS_ENTRY_MODAL_CONFIG, ChargeBasisEntryModalComponent} from './charge-basis-entry-modal.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from 'rxjs/Observable';
import {ChargeBasisUnit} from '../../../../model/application/invoice/charge-basis-unit';


@Component({
  selector: 'charge-basis',
  template: require('./charge-basis.component.html'),
  styles: [
    require('./charge-basis.component.scss')
  ]
})
export class ChargeBasisComponent implements OnInit, OnDestroy {

  @Input() parentForm: FormGroup;
  @Input() applicationId: number;
  chargeBasisEntries: FormArray;

  private rowSubscription = new Subscription();
  private dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>;


  constructor(private fb: FormBuilder, private dialog: MatDialog, private invoiceHub: InvoiceHub) {
    this.chargeBasisEntries = fb.array([]);
  }

  ngOnInit(): void {
    this.parentForm.addControl('chargeBasisEntries', this.chargeBasisEntries);
    this.invoiceHub.loadChargeBasisEntries(this.applicationId)
      .subscribe(entries => {}, error => NotificationService.error(error));

    this.rowSubscription = this.invoiceHub.chargeBasisEntries
      .subscribe(entries => this.entriesUpdated(entries));
  }

  ngOnDestroy(): void {
    this.rowSubscription.unsubscribe();
  }

  newRow(): void {
    this.openModal(new ChargeBasisEntry(ChargeBasisUnit.DAY)).subscribe(entry => {
      this.addEntry(entry);
      this.parentForm.markAsDirty();
    });
  }

  editRow(index: number): void {
    let entry = ChargeBasisEntryForm.toChargeBasisEntry(this.chargeBasisEntries.at(index).value);
    this.openModal(entry)
      .subscribe(updatedEntry => {
        console.log('updatedEntry', updatedEntry);
        this.updateEntry(updatedEntry, index);
        this.parentForm.markAsDirty();
      });
  }

  private entriesUpdated(entries: Array<ChargeBasisEntry>): void {
    this.chargeBasisEntries = this.fb.array([]);
    this.parentForm.setControl('chargeBasisEntries', this.chargeBasisEntries);
    entries.forEach(entry => this.addEntry(entry));
  }

  private addEntry(entry: ChargeBasisEntry): void {
    this.chargeBasisEntries.push(ChargeBasisEntryForm.formGroup(this.fb, entry));
  }

  private updateEntry(entry: ChargeBasisEntry, index: number): void {
    this.chargeBasisEntries.at(index).patchValue(ChargeBasisEntryForm.toFormValue(entry));
  }

  private openModal(entry?: ChargeBasisEntry): Observable<ChargeBasisEntry> {
    const config = {
      ...CHARGE_BASIS_ENTRY_MODAL_CONFIG,
      data: {invoiceRow: entry}
    };

    this.dialogRef = this.dialog.open<ChargeBasisEntryModalComponent>(ChargeBasisEntryModalComponent, config);
    this.dialogRef.componentInstance.chargeBasisEntry = entry;
    return this.dialogRef.afterClosed()
      .filter(r => !!r);
  }
}
