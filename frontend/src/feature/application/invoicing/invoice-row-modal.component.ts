import {Component, OnDestroy, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {EnumUtil} from '../../../util/enum.util';
import {NegligencePaymentType} from '../../../model/application/invoice/negligence-payment-type';
import {InvoiceRowForm} from './invoice-row.form';
import {Subscription} from 'rxjs/Subscription';
import {findTranslation} from '../../../util/translations';
import {InvoiceUnit} from '../../../model/application/invoice/invoice-unit';
import {DEFAULT_FEE_CENTS, InvoiceRow} from '../../../model/application/invoice/invoice-row';

@Component({
  selector: 'invoice-row-modal',
  template: require('./invoice-row-modal.component.html'),
  styles: [
    require('./invoice-row-modal.component.scss')
  ]
})
export class InvoiceRowModalComponent implements OnInit, OnDestroy {

  invoiceRowForm: FormGroup;
  negligencePaymentTypes = EnumUtil.enumValues(NegligencePaymentType);
  invoiceUnits = EnumUtil.enumValues(InvoiceUnit);
  negligencePaymentTypeCtrl: FormControl;

  private negligencePaymentSubscription: Subscription;

  constructor(public dialogRef: MdDialogRef<InvoiceRowModalComponent>, private fb: FormBuilder) {
    this.invoiceRowForm = InvoiceRowForm.formGroup(fb, new InvoiceRow(InvoiceUnit.DAY));
    this.negligencePaymentTypeCtrl = fb.control({});
    this.invoiceRowForm.addControl('negligencePaymentType', this.negligencePaymentTypeCtrl);
  }

  ngOnInit(): void {
    this.negligencePaymentSubscription = this.negligencePaymentTypeCtrl.valueChanges
      .subscribe(type => this.onNegligencePaymentTypeChange(type));
  }

  ngOnDestroy(): void {
    this.negligencePaymentSubscription.unsubscribe();
  }

  onSubmit(form: InvoiceRowForm): void {
    let row = InvoiceRowForm.toInvoiceRow(form);
    row.unitPrice = DEFAULT_FEE_CENTS;
    row.updateNetPrice();
    this.dialogRef.close(row);
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }

  private onNegligencePaymentTypeChange(type: string): void {
    if (NegligencePaymentType.OTHER === NegligencePaymentType[type]) {
      this.invoiceRowForm.patchValue({rowText: ''}, {emitEvent: true});
    } else {
      this.invoiceRowForm.patchValue({rowText: findTranslation(['invoice.negligencePaymentType', type])}, {emitEvent: true});
    }
  }
}
