import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {EnumUtil} from '../../../../util/enum.util';
import {ChargeBasisEntryForm} from './charge-basis-entry.form';
import {findTranslation} from '../../../../util/translations';
import {ChargeBasisUnit} from '../../../../model/application/invoice/charge-basis-unit';
import {ChargeBasisEntry} from '../../../../model/application/invoice/charge-basis-entry';
import {Observable} from 'rxjs/Observable';
import {Some} from '../../../../util/option';
import {NegligenceFeeType} from '../../../../model/application/invoice/negligence-fee-type';
import {Subscription} from 'rxjs/Subscription';
import {NumberUtil} from '../../../../util/number.util';

export const CHARGE_BASIS_ENTRY_MODAL_CONFIG = {width: '800PX', data: {}};

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
  negligenceFeeTypes = EnumUtil.enumValues(NegligenceFeeType).map(t => findTranslation(['invoice.negligenceFeeType', t]));
  unitTypes = EnumUtil.enumValues(ChargeBasisUnit);
  textCtrl: FormControl;
  matchingTexts: Observable<Array<string>>;

  private formSubscription: Subscription;

  constructor(public dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.chargeBasisEntryForm = ChargeBasisEntryForm.formGroup(this.fb, this.chargeBasisEntry);
    this.textCtrl = <FormControl>this.chargeBasisEntryForm.get('text');

    this.formSubscription = this.chargeBasisEntryForm.valueChanges.subscribe(entryForm => this.updateNetPrice(entryForm));

    this.matchingTexts = this.textCtrl.valueChanges
      .startWith(undefined)
      .debounceTime(300)
      .map(text => this.filterNegligenceFeeTypes(text));
  }

  ngOnDestroy(): void {
    this.formSubscription.unsubscribe();
  }

  onSubmit(): void {
    let entry = ChargeBasisEntryForm.toChargeBasisEntry(this.chargeBasisEntryForm.getRawValue());
    entry.manuallySet = true;
    this.dialogRef.close(entry);
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }

  private filterNegligenceFeeTypes(value: string): string[] {
    return Some(value)
      .map(val => this.negligenceFeeTypes
        .filter(type => type.toUpperCase().indexOf(val.toUpperCase()) === 0))
      .orElse(this.negligenceFeeTypes.slice());
  }

  private updateNetPrice(form: ChargeBasisEntryForm) {
    if (NumberUtil.isDefined(form.unitPrice) && NumberUtil.isDefined(form.quantity)) {
      this.chargeBasisEntryForm.patchValue({netPrice: form.unitPrice * form.quantity}, {emitEvent: false});
    } else {
      this.chargeBasisEntryForm.patchValue({netPrice: undefined}, {emitEvent: false});
    }
  }
}
