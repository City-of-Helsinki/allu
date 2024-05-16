import {Component, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormBuilder} from '@angular/forms';
import {MatLegacyDialog as MatDialog, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';
import {CHARGE_BASIS_ENTRY_MODAL_CONFIG, ChargeBasisEntryModalComponent} from './charge-basis-entry-modal.component';
import {Observable, Subject} from 'rxjs';
import {Application} from '@model/application/application';
import {applicationCanBeEdited, invoicingChangesAllowedForType} from '@model/application/application-status';
import {CurrentUser} from '@service/user/current-user';
import {MODIFY_ROLES, RoleType} from '@model/user/role-type';
import {filter, map, take, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import * as fromInvoicing from '@feature/application/invoicing/reducers';
import {AddEntry, Load, RemoveEntry, SetInvoicable, UpdateEntry} from '@feature/application/invoicing/actions/charge-basis-actions';
import {InvoicingPeriod} from '@feature/application/invoicing/invoicing-period/invoicing-period';
import {InvoicingPeriodLength} from '@feature/application/invoicing/invoicing-period/invoicing-period-length';
import {ApplicationType} from '@model/application/type/application-type';

@Component({
  selector: 'charge-basis',
  templateUrl: './charge-basis.component.html',
  styleUrls: [
    './charge-basis.component.scss'
  ]
})
export class ChargeBasisComponent implements OnInit, OnDestroy {

  selectedPeriod$: Observable<InvoicingPeriodLength>;
  invoicingPeriods$: Observable<InvoicingPeriod[]>;
  chargeBasisEntries$: Observable<ChargeBasisEntry[]>;
  calculatedPrice: number;
  changesAllowed = false;
  showPeriodSelect = false;
  periodSelectionDisabled = false;

  private dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>;
  private destroy = new Subject<boolean>();

  constructor(private fb: UntypedFormBuilder,
              private dialog: MatDialog,
              private store: Store<fromRoot.State>,
              private currentUser: CurrentUser) {
  }

  ngOnInit(): void {
    this.store.pipe(
      select(fromApplication.getCurrentApplication),
      takeUntil(this.destroy)
    ).subscribe(app => this.onApplicationChange(app));

    this.chargeBasisEntries$ = this.store.pipe(select(fromInvoicing.getAllChargeBasisEntries));
    this.invoicingPeriods$ = this.store.pipe(select(fromInvoicing.getAllPeriods));
    this.selectedPeriod$ = this.store.pipe(
      select(fromApplication.getCurrentApplication),
      map(app => app.invoicingPeriodLength));

    this.store.dispatch(new Load());

    this.initChangesAllowed();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  newEntry(): void {
    this.openModal().subscribe(entry => this.store.dispatch(new AddEntry(entry)));
  }

  editEntry(entry: ChargeBasisEntry): void {
    this.openModal(entry).subscribe(updatedEntry => this.store.dispatch(new UpdateEntry(updatedEntry)));
  }

  removeEntry(id: number): void {
    this.store.dispatch(new RemoveEntry(id));
  }

  setInvoicable(id: number, invoicable: boolean): void {
    this.store.dispatch(new SetInvoicable(id, invoicable));
  }

  trackById(index: number, entry: ChargeBasisEntry): number {
    return entry.id;
  }

  entriesForPeriod(periodId: number): Observable<ChargeBasisEntry[]> {
    return this.chargeBasisEntries$.pipe(
      map(entries => entries.filter(entry => entry.invoicingPeriodId === periodId))
    );
  }

  private onApplicationChange(app: Application): void {
    this.calculatedPrice = app.calculatedPrice;
    this.showPeriodSelect = app.type === ApplicationType.AREA_RENTAL;
    this.periodSelectionDisabled = !applicationCanBeEdited(app);
  }

  private openModal(entry?: ChargeBasisEntry): Observable<ChargeBasisEntry> {
    const config = { ...CHARGE_BASIS_ENTRY_MODAL_CONFIG, data: { entry: entry } };

    this.dialogRef = this.dialog.open<ChargeBasisEntryModalComponent>(ChargeBasisEntryModalComponent, config);
    return this.dialogRef.afterClosed().pipe(filter(r => !!r));
  }

  private initChangesAllowed() {
    this.store.select(fromApplication.getCurrentApplication).pipe(
      take(1),
      map(app => invoicingChangesAllowedForType(app)),
      withLatestFrom(this.currentUser.hasRole(MODIFY_ROLES.map(role => RoleType[role]))),
      map(([changesAllowed, modifyRole]) => changesAllowed && modifyRole)
    ).subscribe(e => this.changesAllowed = e);
  }
}
