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
import {FormUtil} from '../../../../util/form.util';


@Component({
  selector: 'charge-basis',
  template: require('./charge-basis.component.html'),
  styles: [
    require('./charge-basis.component.scss')
  ]
})
export class ChargeBasisComponent implements OnInit, OnDestroy {

  @Input() applicationId: number;
  form: FormGroup;
  chargeBasisEntries: FormArray;

  private rowSubscription = new Subscription();
  private dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>;


  constructor(private fb: FormBuilder, private dialog: MatDialog, private invoiceHub: InvoiceHub) {
    this.chargeBasisEntries = fb.array([]);
    this.form = this.fb.group({
      chargeBasisEntries: this.chargeBasisEntries
    });
  }

  ngOnInit(): void {
    this.invoiceHub.loadChargeBasisEntries(this.applicationId)
      .subscribe(entries => {}, error => NotificationService.error(error));

    this.rowSubscription = this.invoiceHub.chargeBasisEntries
      .subscribe(entries => this.entriesUpdated(entries));
  }

  ngOnDestroy(): void {
    this.rowSubscription.unsubscribe();
  }

  newEntry(): void {
    this.openModal(new ChargeBasisEntry(ChargeBasisUnit.DAY))
      .switchMap(entry => this.addEntry(entry))
      .subscribe(
        saved => NotificationService.translateMessage('chargeBasis.action.save'),
        error => NotificationService.error(error)
      );
  }

  editEntry(index: number): void {
    const entryForm = <FormGroup>this.chargeBasisEntries.at(index);
    this.openModal(ChargeBasisEntryForm.toChargeBasisEntry(entryForm.getRawValue()))
      .switchMap(updatedEntry => this.updateEntry(updatedEntry, index))
      .subscribe(
          saved => NotificationService.translateMessage('chargeBasis.action.save'),
          error => NotificationService.error(error)
        );
  }

  removeEntry(index: number): void {
    this.chargeBasisEntries.removeAt(index);
    this.saveEntries().subscribe(
      saved => NotificationService.translateMessage('chargeBasis.action.save'),
      error => NotificationService.error(error)
    );
  }

  private entriesUpdated(entries: Array<ChargeBasisEntry>): void {
    FormUtil.clearArray(this.chargeBasisEntries);
    entries.forEach(entry => this.chargeBasisEntries.push(ChargeBasisEntryForm.formGroup(this.fb, entry)));
  }

  private addEntry(entry: ChargeBasisEntry): Observable<Array<ChargeBasisEntry>> {
    this.chargeBasisEntries.push(ChargeBasisEntryForm.formGroup(this.fb, entry));
    return this.saveEntries();
  }

  private updateEntry(entry: ChargeBasisEntry, index: number): Observable<Array<ChargeBasisEntry>> {
    this.chargeBasisEntries.at(index).patchValue(ChargeBasisEntryForm.toFormValue(entry));
    return this.saveEntries();
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

  private saveEntries(): Observable<Array<ChargeBasisEntry>> {
    const entries = this.chargeBasisEntries.getRawValue().map(value => ChargeBasisEntryForm.toChargeBasisEntry(value));
    return this.invoiceHub.saveChargeBasisEntries(this.applicationId, entries);
  }
}
