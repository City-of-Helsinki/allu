import {Component, OnInit} from '@angular/core';
import {ApplicationStore} from '@service/application/application-store';
import {InvoicingInfoForm} from './invoicing-info/invoicing-info.form';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {NotificationService} from '@feature/notification/notification.service';
import {Observable, of, Subject} from 'rxjs';
import {Application} from '@model/application/application';
import {Customer} from '@model/customer/customer';
import {CustomerForm} from '@feature/customerregistry/customer/customer.form';
import {MatDialog} from '@angular/material';
import {ConfirmDialogComponent} from '@feature/common/confirm-dialog/confirm-dialog.component';
import {CanComponentDeactivate} from '@service/common/can-deactivate-guard';
import {findTranslation} from '@util/translations';
import {NumberUtil} from '@util/number.util';
import {CustomerService} from '@service/customer/customer.service';
import {CurrentUser} from '@service/user/current-user';
import {MODIFY_ROLES, RoleType} from '@model/user/role-type';
import {catchError, map, switchMap, take} from 'rxjs/internal/operators';
import {applicationCanBeEdited} from '@model/application/application-status';
import {Store} from '@ngrx/store';
import * as fromApplication from '../reducers';
import {SetRecipient} from '@feature/application/invoicing/actions/invoicing-customer-actions';

@Component({
  selector: 'invoicing',
  templateUrl: './invoicing.component.html',
  styleUrls: []
})
export class InvoicingComponent implements OnInit, CanComponentDeactivate {

  applicationId: number;
  infoForm: FormGroup;
  reset = new Subject<boolean>();

  private recipientForm: FormGroup;
  private notBillableCtrl: FormControl;

  constructor(private applicationStore: ApplicationStore,
              private fb: FormBuilder,
              private customerService: CustomerService,
              private dialog: MatDialog,
              private currentUser: CurrentUser,
              private notification: NotificationService,
              private store: Store<fromApplication.State>) {
  }

  ngOnInit(): void {
    this.applicationId = this.applicationStore.snapshot.application.id;
    this.infoForm = InvoicingInfoForm.initialForm(this.fb);
    this.recipientForm = <FormGroup>this.infoForm.get('invoiceRecipient');
    this.notBillableCtrl = <FormControl>this.infoForm.get('notBillable');
    this.currentUser.hasRole(MODIFY_ROLES.map(role => RoleType[role]))
        .subscribe(hasRequiredRole => {
          if (hasRequiredRole) {
            this.infoForm.enable();
          } else {
            this.infoForm.disable();
          }
        });
  }

  onSubmit(): void {
    this.saveApplicationInfo().subscribe(
      () => this.saved(),
      error => this.notification.error(error));
  }

  cancel(): void {
    this.reset.next(true);
  }

  private saveApplicationInfo(): Observable<Application> {
    return this.saveCustomer().pipe(
      map(customer => customer.id),
      switchMap(customer => this.saveInvoiceRecipient(customer)),
      switchMap(recipient => this.saveInvoicingInfo(recipient))
    );
  }

  private saveCustomer(): Observable<Customer> {
    const billable = !this.notBillableCtrl.value;
    const customer = CustomerForm.toCustomer(this.recipientForm.getRawValue());
    if (!NumberUtil.isDefined(customer.id)) {
      customer.invoicingOnly = true;
    }

    if (billable && this.recipientForm.dirty) {
        return this.customerService.saveCustomer(customer);
    } else {
      return of(customer);
    }
  }

  private saveInvoiceRecipient(customerId: number): Observable<number> {
    this.store.dispatch(new SetRecipient(customerId));
    return of(customerId);
  }

  private saveInvoicingInfo(invoiceRecipientId: number): Observable<Application> {
    return this.store.select(fromApplication.getCurrentApplication).pipe(
      take(1),
      switchMap(app => {
        if (applicationCanBeEdited(app.status)) {
          const invoicingInfo: InvoicingInfoForm = this.infoForm.getRawValue();
          app.notBillable = invoicingInfo.notBillable;
          app.notBillableReason = invoicingInfo.notBillable ? invoicingInfo.notBillableReason : undefined;
          app.customerReference = invoicingInfo.customerReference;
          app.invoicingDate = invoicingInfo.invoicingDate;
          app.skipPriceCalculation = invoicingInfo.skipPriceCalculation;
          app.invoiceRecipientId = invoicingInfo.notBillable ? undefined : invoiceRecipientId;
          return this.applicationStore.save(app);
        } else {
          return of(app);
        }
      })
    );
  }

  private saved(): void {
    this.notification.translateSuccess('invoice.action.save');
    this.infoForm.markAsPristine();
  }

  canDeactivate(): Observable<boolean> | boolean {
    if (this.infoForm.dirty) {
      return this.confirmChanges();
    } else {
      return true;
    }
  }

  private confirmChanges(): Observable<boolean> {
    const confirmType = this.infoForm.valid ? 'confirmSave' : 'confirmDiscard';
    const data = {
      title: findTranslation(['invoice', confirmType, 'title']),
      description: findTranslation(['invoice', confirmType, 'description']),
      confirmText: findTranslation(['invoice', confirmType, 'confirmText']),
      cancelText: findTranslation(['invoice', confirmType, 'cancelText'])
    };

    if (this.infoForm.valid) {
      return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed().pipe(
        switchMap(save => this.saveChanges(save))
      );
    } else {
      return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed();
    }
  }

  private saveChanges(save: boolean): Observable<boolean> {
    if (save) {
      return this.saveApplicationInfo().pipe(
        map(() => {
          this.notification.translateSuccess('invoice.action.save');
          return true;
        }),
        catchError(error => {
          this.notification.error(error);
          return of(false);
        })
      );
    } else {
      return of(true);
    }
  }
}
