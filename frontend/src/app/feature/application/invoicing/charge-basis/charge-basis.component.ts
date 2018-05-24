import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ChargeBasisEntry} from '../../../../model/application/invoice/charge-basis-entry';
import {InvoiceHub} from '../../../../service/application/invoice/invoice-hub';
import {ChargeBasisEntryForm} from './charge-basis-entry.form';
import {CHARGE_BASIS_ENTRY_MODAL_CONFIG, ChargeBasisEntryModalComponent} from './charge-basis-entry-modal.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {combineLatest, Observable, Subject} from 'rxjs';
import {FormUtil} from '../../../../util/form.util';
import {ChargeBasisType} from '../../../../model/application/invoice/charge-basis-type';
import {ChargeBasisUnit} from '../../../../model/application/invoice/charge-basis-unit';
import {ApplicationStore} from '../../../../service/application/application-store';
import {Application} from '../../../../model/application/application';
import {applicationCanBeEdited} from '../../../../model/application/application-status';
import {CurrentUser} from '../../../../service/user/current-user';
import {MODIFY_ROLES, RoleType} from '../../../../model/user/role-type';
import {filter, map, switchMap, takeUntil, tap} from 'rxjs/internal/operators';

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
  calculatedPrice: number;
  canBeEdited = false;

  private dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>;
  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private dialog: MatDialog,
              private invoiceHub: InvoiceHub,
              private applicationStore: ApplicationStore,
              private currentUser: CurrentUser,
              private notification: NotificationService) {
    this.chargeBasisEntries = fb.array([]);
    this.form = this.fb.group({
      chargeBasisEntries: this.chargeBasisEntries
    });
  }

  ngOnInit(): void {
    this.applicationStore.application.pipe(takeUntil(this.destroy))
      .subscribe(app => this.onApplicationChange(app));

    this.invoiceHub.chargeBasisEntries.pipe(takeUntil(this.destroy))
      .subscribe(entries => this.entriesUpdated(entries));

    combineLatest(
      this.applicationStore.application,
      this.currentUser.hasRole(MODIFY_ROLES.map(role => RoleType[role]))
    ).pipe(
      map(([app, role]) => applicationCanBeEdited(app.statusEnum) && role)
    ).subscribe(e => this.canBeEdited = e);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  newEntry(): void {
    this.openModal().pipe(
      switchMap(entry => this.addEntry(entry))
    ).subscribe(
        saved => this.notification.translateSuccess('chargeBasis.action.save'),
        error => this.notification.errorInfo(error)
      );
  }

  editEntry(index: number): void {
    const entryForm = <FormGroup>this.chargeBasisEntries.at(index);
    this.openModal(ChargeBasisEntryForm.toChargeBasisEntry(entryForm.getRawValue())).pipe(
      switchMap(updatedEntry => this.updateEntry(updatedEntry, index))
    ).subscribe(
      saved => this.notification.translateSuccess('chargeBasis.action.save'),
      error => this.notification.errorInfo(error)
    );
  }

  removeEntry(index: number): void {
    this.chargeBasisEntries.removeAt(index);
    this.saveEntries().subscribe(
      saved => this.notification.translateSuccess('chargeBasis.action.save'),
      error => this.notification.errorInfo(error)
    );
  }

  showMinimal(value: ChargeBasisEntryForm): boolean {
    return value.type === ChargeBasisType[ChargeBasisType.DISCOUNT]
      || value.unit === ChargeBasisUnit[ChargeBasisUnit.PERCENT];
  }

  entryValue(entry: ChargeBasisEntryForm): string {
    let prefix = '';
    let value = entry.unitPrice;
    if (entry.unit === 'PERCENT') {
      value = entry.quantity;
    }
    if (ChargeBasisType[entry.type] === ChargeBasisType.DISCOUNT) {
      if (value < 0) {
        prefix = '+';
        value = -value;
      } else {
        prefix = '-';
      }
    }
    return prefix + value;
  }

  private onApplicationChange(app: Application): void {
    this.calculatedPrice = app.calculatedPriceEuro;

    this.invoiceHub.loadChargeBasisEntries(app.id).pipe(takeUntil(this.destroy))
      .subscribe(() => {}, error => this.notification.errorInfo(error));
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
    return this.dialogRef.afterClosed().pipe(filter(r => !!r));
  }

  private saveEntries(): Observable<Array<ChargeBasisEntry>> {
    const entries = this.chargeBasisEntries.getRawValue().map(value => ChargeBasisEntryForm.toChargeBasisEntry(value));
    const appId = this.applicationStore.snapshot.application.id;
    return this.invoiceHub.saveChargeBasisEntries(appId, entries).pipe(
      tap(() => this.applicationStore.load(appId).subscribe())
    );
  }
}
