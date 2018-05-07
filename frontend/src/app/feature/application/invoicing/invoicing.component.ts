import {Component, OnDestroy, OnInit} from '@angular/core';
import {ApplicationStore} from '../../../service/application/application-store';
import {InvoicingInfoForm} from './invoicing-info/invoicing-info.form';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {NotificationService} from '../../../service/notification/notification.service';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../../model/application/application';
import {Customer} from '../../../model/customer/customer';
import {CustomerForm} from '../../customerregistry/customer/customer.form';
import {Subject} from 'rxjs/Subject';
import {MatDialog} from '@angular/material';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {CanComponentDeactivate} from '../../../service/common/can-deactivate-guard';
import {findTranslation} from '../../../util/translations';
import {NumberUtil} from '../../../util/number.util';
import {CustomerService} from '../../../service/customer/customer.service';
import {CurrentUser} from '../../../service/user/current-user';
import {RoleType, MODIFY_ROLES} from '../../../model/user/role-type';

@Component({
  selector: 'invoicing',
  templateUrl: './invoicing.component.html',
  styleUrls: []
})
export class InvoicingComponent implements OnInit, OnDestroy, CanComponentDeactivate {

  applicationId: number;
  infoForm: FormGroup;
  reset = new Subject<boolean>();

  private recipientForm: FormGroup;
  private notBillableCtrl: FormControl;
  private destroy = new Subject<boolean>();

  constructor(private applicationStore: ApplicationStore,
              private fb: FormBuilder,
              private customerService: CustomerService,
              private dialog: MatDialog,
              private currentUser: CurrentUser,
              private notification: NotificationService) {
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

  ngOnDestroy(): void {
    this.destroy.next(true);
  }

  onSubmit(): void {
    this.saveApplicationInfo()
      .subscribe(
        () => this.saved(),
        error => this.notification.error(error));
  }

  cancel(): void {
    this.reset.next(true);

  }

  private saveApplicationInfo(): Observable<Application> {
    const application = this.applicationStore.snapshot.application;

    const invoicingInfo: InvoicingInfoForm = this.infoForm.getRawValue();
    application.notBillable = invoicingInfo.notBillable;
    application.notBillableReason = invoicingInfo.notBillable ? invoicingInfo.notBillableReason : undefined;
    application.customerReference = invoicingInfo.customerReference;
    application.invoicingDate = invoicingInfo.invoicingDate;
    application.skipPriceCalculation = invoicingInfo.skipPriceCalculation;

    return this.saveCustomer()
      .switchMap(customer => {
        application.invoiceRecipientId = customer.id;
        return this.applicationStore.save(application);
      });
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
      return Observable.of(customer);
    }
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
      return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed()
        .switchMap(save => this.saveChanges(save));
    } else {
      return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed();
    }
  }

  private saveChanges(save: boolean): Observable<boolean> {
    if (save) {
      return this.saveApplicationInfo()
        .map(() => {
          this.notification.translateSuccess('invoice.action.save');
          return true;
        }).catch(error => {
          this.notification.error(error);
          return Observable.of(false);
        });
    } else {
      return Observable.of(true);
    }
  }
}
