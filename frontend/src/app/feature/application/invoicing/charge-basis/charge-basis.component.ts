import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ChargeBasisEntry} from '../../../../model/application/invoice/charge-basis-entry';
import {InvoiceHub} from '../../../../service/application/invoice/invoice-hub';
import {ChargeBasisEntryForm} from './charge-basis-entry.form';
import {CHARGE_BASIS_ENTRY_MODAL_CONFIG, ChargeBasisEntryModalComponent} from './charge-basis-entry-modal.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {Observable} from 'rxjs/Observable';
import {FormUtil} from '../../../../util/form.util';
import {ChargeBasisType} from '../../../../model/application/invoice/charge-basis-type';
import {ChargeBasisUnit} from '../../../../model/application/invoice/charge-basis-unit';
import {ApplicationStore} from '../../../../service/application/application-store';
import {Subject} from 'rxjs/Subject';


@Component({
  selector: 'charge-basis',
  templateUrl: './charge-basis.component.html',
  styleUrls: [
    './charge-basis.component.scss'
  ]
})
export class ChargeBasisComponent implements OnInit, OnDestroy {
  form: FormGroup;
  chargeBasisEntries: FormArray;
  calculatedPrice: Observable<number>;

  private dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>;
  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private dialog: MatDialog,
              private invoiceHub: InvoiceHub,
              private applicationStore: ApplicationStore) {
    this.chargeBasisEntries = fb.array([]);
    this.form = this.fb.group({
      chargeBasisEntries: this.chargeBasisEntries
    });
  }

  ngOnInit(): void {
    this.applicationStore.application
      .takeUntil(this.destroy)
      .switchMap(app => this.invoiceHub.loadChargeBasisEntries(app.id))
      .subscribe(entries => {}, error => NotificationService.error(error));

    this.invoiceHub.chargeBasisEntries
      .takeUntil(this.destroy)
      .subscribe(entries => this.entriesUpdated(entries));

    this.calculatedPrice = this.applicationStore.application.map(app => app.calculatedPriceEuro);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  newEntry(): void {
    this.openModal()
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

  showMinimal(value: ChargeBasisEntryForm): boolean {
    return value.type === ChargeBasisType[ChargeBasisType.DISCOUNT]
      || value.unit === ChargeBasisUnit[ChargeBasisUnit.PERCENT];
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
    const config = { ...CHARGE_BASIS_ENTRY_MODAL_CONFIG, data: { entry: entry } };

    this.dialogRef = this.dialog.open<ChargeBasisEntryModalComponent>(ChargeBasisEntryModalComponent, config);
    return this.dialogRef.afterClosed()
      .filter(r => !!r);
  }

  private saveEntries(): Observable<Array<ChargeBasisEntry>> {
    const entries = this.chargeBasisEntries.getRawValue().map(value => ChargeBasisEntryForm.toChargeBasisEntry(value));
    const appId = this.applicationStore.snapshot.application.id;
    return this.invoiceHub.saveChargeBasisEntries(appId, entries)
      .do(e => this.applicationStore.load(appId).subscribe());
  }
}
