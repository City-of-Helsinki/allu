import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {EnumUtil} from '../../../../util/enum.util';
import {NegligencePaymentType} from '../../../../model/application/invoice/negligence-payment-type';
import {ChargeBasisEntryForm} from './charge-basis-entry.form';
import {findTranslation} from '../../../../util/translations';
import {ChargeBasisUnit} from '../../../../model/application/invoice/charge-basis-unit';
import {ChargeBasisEntry, DEFAULT_FEE_CENTS} from '../../../../model/application/invoice/charge-basis-entry';
import {Observable} from 'rxjs/Observable';
import {Some} from '../../../../util/option';

export const CHARGE_BASIS_ENTRY_MODAL_CONFIG = {width: '600PX', data: {}};

@Component({
  selector: 'charge-basis-entry-modal',
  template: require('./charge-basis-entry-modal.component.html'),
  styles: [
    require('./charge-basis-entry-modal.component.scss')
  ]
})
export class ChargeBasisEntryModalComponent implements OnInit, OnDestroy {

  @Input() chargeBasisEntry: ChargeBasisEntry = new ChargeBasisEntry(ChargeBasisUnit.DAY);

  chargeBasisEntryForm: FormGroup;
  negligencePaymentTypes = EnumUtil.enumValues(NegligencePaymentType)
    .map(t => findTranslation(['invoice.negligencePaymentType', t]));
  textCtrl: FormControl;
  matchingTexts: Observable<Array<string>>;

  constructor(public dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.chargeBasisEntryForm = ChargeBasisEntryForm.formGroup(this.fb, this.chargeBasisEntry);
    this.textCtrl = <FormControl>this.chargeBasisEntryForm.get('text');

    this.matchingTexts = this.textCtrl.valueChanges
      .startWith(undefined)
      .debounceTime(300)
      .map(text => this.filterNegligencePaymentTypes(text));
  }

  ngOnDestroy(): void {
  }

  onSubmit(form: ChargeBasisEntryForm): void {
    let entry = ChargeBasisEntryForm.toChargeBasisEntry(form);
    entry.unitPrice = DEFAULT_FEE_CENTS;
    entry.updateNetPrice();
    entry.manuallySet = true;
    this.dialogRef.close(entry);
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }

  private filterNegligencePaymentTypes(value: string): string[] {
    return Some(value)
      .map(val => this.negligencePaymentTypes
        .filter(type => type.toUpperCase().indexOf(val.toUpperCase()) === 0))
      .orElse(this.negligencePaymentTypes.slice());
  }
}
