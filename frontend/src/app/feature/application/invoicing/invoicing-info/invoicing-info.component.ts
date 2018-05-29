import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {ApplicationStore} from '../../../../service/application/application-store';
import {Some} from '../../../../util/option';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Application} from '../../../../model/application/application';
import {ALWAYS_ENABLED_FIELDS} from '../../../customerregistry/customer/customer-info.component';
import {
  CUSTOMER_MODAL_CONFIG,
  CustomerModalComponent
} from '../../../customerregistry/customer/customer-modal.component';
import {MatDialog} from '@angular/material';
import {NumberUtil} from '../../../../util/number.util';
import {DEPOSIT_MODAL_CONFIG, DepositModalComponent} from '../deposit/deposit-modal.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {Deposit} from '../../../../model/application/invoice/deposit';
import {DepositStatusType} from '../../../../model/application/invoice/deposit-status-type';
import {applicationCanBeEdited, ApplicationStatus} from '../../../../model/application/application-status';
import {InvoicingInfoForm} from './invoicing-info.form';
import {CustomerService} from '../../../../service/customer/customer.service';
import {MODIFY_ROLES, RoleType} from '../../../../model/user/role-type';
import {filter, map, switchMap, take, tap} from 'rxjs/internal/operators';
import {Store} from '@ngrx/store';
import * as fromApplication from '../../reducers';
import {ApplicationTagType} from '../../../../model/application/tag/application-tag-type';
import {TimeUtil} from '../../../../util/time.util';

@Component({
  selector: 'invoicing-info',
  templateUrl: './invoicing-info.component.html',
  styleUrls: [
    './invoicing-info.component.scss'
  ]
})
export class InvoicingInfoComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() reset: Observable<boolean>;

  MODIFY_ROLES = MODIFY_ROLES.map(role => RoleType[role]);

  recipientForm: FormGroup;
  applicationLoaded: Observable<boolean>;

  private notBillableCtrl: FormControl;
  private notBillableReasonCtrl: FormControl;
  private invoicingDateCtrl: FormControl;
  private originalForm: InvoicingInfoForm;
  private originalRecipientForm: CustomerForm;

  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromApplication.State>,
              private customerService: CustomerService,
              private dialog: MatDialog,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.recipientForm = <FormGroup>this.form.get('invoiceRecipient');
    this.notBillableCtrl = <FormControl>this.form.get('notBillable');
    this.notBillableReasonCtrl = <FormControl>this.form.get('notBillableReason');
    this.invoicingDateCtrl = <FormControl>this.form.get('invoicingDate');
    this.notBillableCtrl.valueChanges.subscribe(value => this.onNotBillableChange(value));
    this.initForm();
    this.reset.pipe(filter(r => !!r)).subscribe(r => this.resetMe(r));
    this.applicationLoaded = this.store.select(fromApplication.getApplicationLoaded);
  }

  invoiceRecipientChange(recipient: CustomerForm) {
    if (recipient.id) {
      this.setCustomerEdit();
    }
  }

  editCustomer(): void {
    const customerModalRef = this.dialog.open<CustomerModalComponent>(CustomerModalComponent, CUSTOMER_MODAL_CONFIG);
    customerModalRef.componentInstance.customerId = this.recipientForm.value.id;
    customerModalRef.afterClosed().pipe(filter(customer => !!customer))
      .subscribe(customer => this.recipientForm.patchValue(CustomerForm.fromCustomer(customer)));
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

  customerCanBeEdited(): boolean {
    return NumberUtil.isDefined(this.recipientForm.value.id) && this.billable;
  }

  get invoicingProhibited(): boolean {
    return this.recipientForm.getRawValue().invoicingProhibited && this.billable;
  }

  private initForm(): void {
    this.store.select(fromApplication.getCurrentApplication).pipe(take(1))
      .subscribe(app => {
        Some(app.invoiceRecipientId).do(id => this.findAndPatchCustomer(id));
        const invoicingDate = app.invoicingDate ? app.invoicingDate : this.defaultInvoicingDate(app);

        this.form.patchValue({
          notBillable: app.notBillable,
          notBillableReason: app.notBillableReason,
          customerReference: app.customerReference,
          invoicingDate: invoicingDate,
          skipPriceCalculation: app.skipPriceCalculation
        });
        this.originalForm = this.form.getRawValue();

        this.setEditable(app.statusEnum, invoicingDate);
    });

    this.initDeposit();
  }

  private setEditable(status: ApplicationStatus, invoicingDate: Date) {
    if (!applicationCanBeEdited(status)) {
      this.form.disable();

      this.invoiceRecipientCanBeEdited(status, invoicingDate)
        .pipe(filter(canBeEdited => canBeEdited))
        .subscribe(() => this.setCustomerEdit());
    }
  }

  private findAndPatchCustomer(id: number): void {
    this.setCustomerEdit();
    this.customerService.findCustomerById(id)
      .subscribe(customer => {
        this.recipientForm.patchValue(CustomerForm.fromCustomer(customer), {emitEvent: false});
        this.originalRecipientForm = this.recipientForm.getRawValue();
    });
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
    Object.keys(this.recipientForm.controls).forEach(key => {
      const field = this.recipientForm.get(key);
      if (ALWAYS_ENABLED_FIELDS.indexOf(key) >= 0) {
        field.enable({emitEvent: false});
      } else {
        field.disable({emitEvent: false});
      }
    });
  }

  private currentDeposit(): Deposit {
    const applicationId = this.applicationStore.snapshot.application.id;
    return this.applicationStore.snapshot.deposit || Deposit.forApplication(applicationId);
  }

  private defaultInvoicingDate(application: Application): Date {
    const currentDate = new Date();
    const result = new Date(application.startTime);
    result.setDate(result.getDate() - 15);
    return result > currentDate ? result : currentDate;
  }

  private resetMe(reset: boolean) {
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

  private invoiceRecipientCanBeEdited(status: ApplicationStatus, invoicingDate: Date): Observable<boolean> {
    const waitingForDecision = ApplicationStatus.DECISIONMAKING === status;
    const decision = ApplicationStatus.DECISION === status;
    const tomorrow = TimeUtil.toStartDate(TimeUtil.addDays(new Date(), 1));
    const dayBeforeInvoicing = !TimeUtil.isBefore(invoicingDate, tomorrow);
    const editableAfterDecision = decision && dayBeforeInvoicing;

    return this.store.select(fromApplication.hasTag(ApplicationTagType.SAP_ID_MISSING)).pipe(
      map(sapIdMissing => waitingForDecision || (sapIdMissing && editableAfterDecision))
    );
  }
}
