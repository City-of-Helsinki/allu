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
import {Application} from '../../../../model/application/application';
import {applicationCanBeEdited} from '../../../../model/application/application-status';
import {CurrentUser} from '../../../../service/user/current-user';
import {MODIFY_ROLES, RoleType} from '../../../../model/user/role-type';

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
  canBeEdited = true;
  modifyRole = false;

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
    this.applicationStore.application
      .takeUntil(this.destroy)
      .subscribe(app => this.onApplicationChange(app));

    this.invoiceHub.chargeBasisEntries
      .takeUntil(this.destroy)
      .subscribe(entries => this.entriesUpdated(entries));

    this.currentUser.hasRole(MODIFY_ROLES.map(role => RoleType[role]))
      .subscribe(hasRequiredRole => this.modifyRole = hasRequiredRole);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  newEntry(): void {
    this.openModal()
      .switchMap(entry => this.addEntry(entry))
      .subscribe(
        saved => this.notification.translateSuccess('chargeBasis.action.save'),
        error => this.notification.errorInfo(error)
      );
  }

  editEntry(index: number): void {
    const entryForm = <FormGroup>this.chargeBasisEntries.at(index);
    this.openModal(ChargeBasisEntryForm.toChargeBasisEntry(entryForm.getRawValue()))
      .switchMap(updatedEntry => this.updateEntry(updatedEntry, index))
      .subscribe(
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

  private onApplicationChange(app: Application): void {
    this.calculatedPrice = app.calculatedPriceEuro;
    this.canBeEdited = applicationCanBeEdited(app.statusEnum) && this.modifyRole;

    this.invoiceHub.loadChargeBasisEntries(app.id)
      .takeUntil(this.destroy)
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
