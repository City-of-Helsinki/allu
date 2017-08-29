import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {EnumUtil} from '../../../util/enum.util';
import {NegligencePaymentType} from '../../../model/application/invoice/negligence-payment-type';
import {InvoiceRowForm} from './invoice-row.form';
import {findTranslation} from '../../../util/translations';
import {InvoiceUnit} from '../../../model/application/invoice/invoice-unit';
import {DEFAULT_FEE_CENTS, InvoiceRow} from '../../../model/application/invoice/invoice-row';
import {Observable} from 'rxjs/Observable';
import {StringUtil} from '../../../util/string.util';

@Component({
  selector: 'invoice-row-modal',
  template: require('./invoice-row-modal.component.html'),
  styles: [
    require('./invoice-row-modal.component.scss')
  ]
})
export class InvoiceRowModalComponent implements OnInit, OnDestroy {

  @Input() invoiceRow: InvoiceRow = new InvoiceRow(InvoiceUnit.DAY);

  invoiceRowForm: FormGroup;
  negligencePaymentTypes = EnumUtil.enumValues(NegligencePaymentType)
    .map(t => findTranslation(['invoice.negligencePaymentType', t]));
  invoiceUnits = EnumUtil.enumValues(InvoiceUnit);
  rowTextCtrl: FormControl;
  matchingRowTexts: Observable<Array<string>>;

  constructor(public dialogRef: MdDialogRef<InvoiceRowModalComponent>, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.invoiceRowForm = InvoiceRowForm.formGroup(this.fb, this.invoiceRow);
    this.rowTextCtrl = <FormControl>this.invoiceRowForm.get('rowText');

    this.matchingRowTexts = this.rowTextCtrl.valueChanges
      .debounceTime(300)
      .map(text => StringUtil.toUppercase(text))
      .map(text => this.negligencePaymentTypes.filter(types => StringUtil.toUppercase(types).indexOf(text) >= 0));
  }

  ngOnDestroy(): void {
  }

  onSubmit(form: InvoiceRowForm): void {
    let row = InvoiceRowForm.toInvoiceRow(form);
    row.unitPrice = DEFAULT_FEE_CENTS;
    row.updateNetPrice();
    row.manuallySet = true;
    this.dialogRef.close(row);
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }
}
