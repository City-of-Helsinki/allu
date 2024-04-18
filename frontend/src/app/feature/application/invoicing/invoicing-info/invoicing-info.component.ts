import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {Observable, Subject} from 'rxjs';
import {ApplicationStore} from '@service/application/application-store';
import {CustomerForm} from '@feature/customerregistry/customer/customer.form';
import {ALWAYS_ENABLED_FIELDS} from '@feature/customerregistry/customer/customer-info.component';
import {MatDialog} from '@angular/material/dialog';
import {DEPOSIT_MODAL_CONFIG, DepositModalComponent} from '../deposit/deposit-modal.component';
import {NotificationService} from '@feature/notification/notification.service';
import {Deposit} from '@model/application/invoice/deposit';
import {DepositStatusType} from '@model/application/invoice/deposit-status-type';
import {applicationCanBeEdited, ApplicationStatus, isSameOrBefore} from '@model/application/application-status';
import {InvoicingInfoForm} from './invoicing-info.form';
import {MODIFY_ROLES, RoleType} from '@model/user/role-type';
import {filter, map, switchMap, take, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import {select, Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import * as fromInvoicing from '@feature/application/invoicing/reducers';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';
import {TimeUtil} from '@util/time.util';
import {Customer} from '@model/customer/customer';
import {ApplicationType} from '@app/model/application/type/application-type';
import {Application} from '@model/application/application';
import {ArrayUtil} from '@util/array-util';
import {terraceKinds} from '@app/model/application/type/application-kind';
import {Invoice} from '@model/application/invoice/invoice';
import {flexDirectionColumn, flexDirectionRow} from '@feature/common/layout/fxLayout';
import {AreaRental, isAreaRental} from '@model/application/area-rental/area-rental';
import {ApplicationExtension} from '@model/application/type/application-extension';

@Component({
  selector: 'invoicing-info',
  templateUrl: './invoicing-info.component.html',
  styleUrls: [
    './invoicing-info.component.scss'
  ]
})
export class InvoicingInfoComponent implements OnInit, OnDestroy {

  @Input() form: UntypedFormGroup;

  MODIFY_ROLES = MODIFY_ROLES.map(role => RoleType[role]);

  recipientForm: UntypedFormGroup;
  showDeposit: boolean;
  showInvoicingDate: boolean;
  applicationType: ApplicationType;
  customerLoading$: Observable<boolean>;

  private notBillableCtrl: UntypedFormControl;
  private notBillableReasonCtrl: UntypedFormControl;
  private invoicingDateCtrl: UntypedFormControl;
  private originalForm: InvoicingInfoForm;
  private originalRecipientForm: CustomerForm;
  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromApplication.State>,
              private dialog: MatDialog,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.recipientForm = <UntypedFormGroup>this.form.get('invoiceRecipient');
    this.notBillableCtrl = <UntypedFormControl>this.form.get('notBillable');
    this.notBillableReasonCtrl = <UntypedFormControl>this.form.get('notBillableReason');
    this.invoicingDateCtrl = <UntypedFormControl>this.form.get('invoicingDate');
    this.notBillableCtrl.valueChanges.subscribe(value => this.onNotBillableChange(value));
    this.initForm();
    this.customerLoading$ = this.store.pipe(select(fromInvoicing.getInvoicingCustomerLoading));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  reset() {
    if (this.originalForm) {
      this.form.reset(this.originalForm);
    } else {
      this.form.reset();
    }
    if (this.originalRecipientForm) {
      this.recipientForm.reset(this.originalRecipientForm);
    } else {
      this.recipientForm.reset();
    }
  }

  invoiceRecipientChange(recipient: CustomerForm) {
    if (recipient.id) {
      this.setCustomerEdit();
    }
  }

  editDeposit(): void {
    const deposit = this.currentDeposit();
    const config = {...DEPOSIT_MODAL_CONFIG, data: {deposit: deposit}};

    const depositModalRef = this.dialog.open<DepositModalComponent>(DepositModalComponent, config);
    depositModalRef.afterClosed().pipe(
      filter(result => !!result),
      switchMap(result => this.applicationStore.saveDeposit(result))
    ).subscribe(
      result => this.notification.translateSuccess('deposit.action.save'),
      error => this.notification.errorInfo(error));
  }

  nextDepositStatus(): void {
    const deposit = this.currentDeposit();
    deposit.status = deposit.status + 1;
    this.applicationStore.saveDeposit(deposit)
      .subscribe(
        result => this.notification.translateSuccess('deposit.action.save'),
        error => this.notification.errorInfo(error));
  }

  get hasDeposit(): boolean {
    return !!this.form.getRawValue().depositStatus;
  }

  get canChangeDepositStatus(): boolean {
    const deposit = this.applicationStore.snapshot.deposit;
    return deposit ? deposit.status < DepositStatusType.RETURNED_DEPOSIT : false;
  }

  get billable(): boolean {
    return !this.notBillableCtrl.value;
  }

  get detailsDepositDirection(): string {
    return this.billable ? flexDirectionColumn : flexDirectionRow;
  }

  get invoicingProhibited(): boolean {
    return this.recipientForm.getRawValue().invoicingProhibited && this.billable;
  }

  private initForm(): void {
    this.store.pipe(
      select(fromApplication.getCurrentApplication),
      take(1),
    ).subscribe(app => {
      this.form.patchValue({
        notBillable: app.notBillable,
        notBillableReason: app.notBillableReason,
        customerReference: app.customerReference,
        invoicingDate: app.invoicingDate,
        skipPriceCalculation: app.skipPriceCalculation
      });
      this.originalForm = this.form.getRawValue();

      this.setEditable(app);
      this.showDeposit = !ArrayUtil.contains([ApplicationType.AREA_RENTAL, ApplicationType.EXCAVATION_ANNOUNCEMENT], app.type);
      this.showInvoicingDate = !ArrayUtil.contains([ApplicationType.AREA_RENTAL, ApplicationType.EXCAVATION_ANNOUNCEMENT], app.type)
      && !ArrayUtil.anyMatch(terraceKinds, app.kinds);
      this.applicationType = app.type;

      this.initForExtension(app.extension);
    });

    this.store.select(fromInvoicing.getInvoicingCustomer).pipe(takeUntil(this.destroy))
      .subscribe(customer => this.patchCustomer(customer));

    this.initDeposit();
  }

  private setEditable(application: Application) {
    if (!applicationCanBeEdited(application)) {
      this.form.disable();
      this.setCustomerEdit();
    }
    if ([ApplicationType.EXCAVATION_ANNOUNCEMENT, ApplicationType.AREA_RENTAL].indexOf(application.type) > -1) {
      this.invoicingDateCtrl.disable();
    }
  }

  private patchCustomer(customer: Customer = new Customer()): void {
    this.setCustomerEdit();
    this.recipientForm.patchValue(CustomerForm.fromCustomer(customer), {emitEvent: false});
    this.originalRecipientForm = this.recipientForm.getRawValue();
  }

  private onNotBillableChange(notBillable: boolean) {
    if (notBillable) {
      this.invoicingDateCtrl.clearValidators();
      this.notBillableReasonCtrl.setValidators([Validators.required]);
      this.form.removeControl('invoiceRecipient');
    } else {
      this.invoicingDateCtrl.setValidators([Validators.required]);
      this.notBillableReasonCtrl.clearValidators();
      this.form.addControl('invoiceRecipient', this.recipientForm);
    }
  }

  private setCustomerEdit(): void {
    this.invoiceRecipientCanBeEdited().pipe(
      filter(canBeEdited => canBeEdited)
    ).subscribe(() => {
      Object.keys(this.recipientForm.controls).forEach(key => {
        const field = this.recipientForm.get(key);
        if (ALWAYS_ENABLED_FIELDS.indexOf(key) >= 0) {
          field.enable({emitEvent: false});
        } else {
          field.disable({emitEvent: false});
        }
      });
    });
  }

  private currentDeposit(): Deposit {
    const applicationId = this.applicationStore.snapshot.application.id;
    return this.applicationStore.snapshot.deposit || Deposit.forApplication(applicationId);
  }

  private initDeposit(): void {
    this.applicationStore.deposit.pipe(filter(deposit => !!deposit))
      .subscribe(deposit => this.form.patchValue({
          depositAmount: deposit.amountEuro,
          depositReason: deposit.reason,
          depositStatus: deposit.uiStatus
        })
      );

    this.applicationStore.loadDeposit().subscribe();
  }

  private invoiceRecipientCanBeEdited(): Observable<boolean> {
    return this.store.pipe(
      select(fromApplication.getCurrentApplication),
      withLatestFrom(
        this.store.pipe(select(fromApplication.hasTag(ApplicationTagType.SAP_ID_MISSING))),
        this.store.pipe(select(fromInvoicing.getEarliestInvoicable))
      ),
      takeUntil(this.destroy),
      map(([app, sapIdMissing, earliest]) => {
        const noPendingData = app.clientApplicationData === undefined;
        const editableByStatus = isSameOrBefore(app.status, ApplicationStatus.DECISIONMAKING);
        const dayBeforeInvoicing = this.isDayBeforeInvoicing(earliest);
        return (sapIdMissing || editableByStatus || dayBeforeInvoicing) && noPendingData;
      })
    );
  }

  private isDayBeforeInvoicing(earliestInvoice: Invoice): boolean {
    const tomorrow = TimeUtil.toStartDate(TimeUtil.addDays(new Date(), 1));
    return earliestInvoice
      ? !TimeUtil.isBefore(earliestInvoice.invoicableTime, tomorrow)
      : true;
  }

  private initForExtension(extension: ApplicationExtension) {
    if (isAreaRental(extension)) {
      this.form.patchValue({majorDisturbance: extension.majorDisturbance});
    }
  }
}
