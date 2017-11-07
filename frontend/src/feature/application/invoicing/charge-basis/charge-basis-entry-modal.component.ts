import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
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
import {ChargeBasisType, manualChargeBasisTypes} from '../../../../model/application/invoice/charge-basis-type';

export const CHARGE_BASIS_ENTRY_MODAL_CONFIG = {width: '800PX', data: {}};

@Component({
  selector: 'charge-basis-entry-modal',
  template: require('./charge-basis-entry-modal.component.html'),
  styles: [
    require('./charge-basis-entry-modal.component.scss')
  ]
})
export class ChargeBasisEntryModalComponent implements OnInit, OnDestroy {
  chargeBasisEntryForm: FormGroup;
  negligenceFeeTypes = EnumUtil.enumValues(NegligenceFeeType).map(t => findTranslation(['invoice.negligenceFeeType', t]));
  chargeBasisTypes = manualChargeBasisTypes.map(type => ChargeBasisType[type]);
  unitTypes = EnumUtil.enumValues(ChargeBasisUnit);

  textCtrl: FormControl;
  typeCtrl: FormControl;
  matchingTexts: Observable<Array<string>>;

  private formSubscription: Subscription;

  constructor(public dialogRef: MatDialogRef<ChargeBasisEntryModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: ChargeBasisEntryModalData,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    let entry = this.data.entry || new ChargeBasisEntry(ChargeBasisType.NEGLIGENCE_FEE, ChargeBasisUnit.DAY);
    this.chargeBasisEntryForm = ChargeBasisEntryForm.formGroup(this.fb, entry);
    this.typeCtrl = <FormControl>this.chargeBasisEntryForm.get('type');
    this.textCtrl = <FormControl>this.chargeBasisEntryForm.get('text');

    this.formSubscription = this.chargeBasisEntryForm.valueChanges.subscribe(entryForm => this.updateNetPrice(entryForm));
    this.matchingTexts = this.typeCtrl.valueChanges
      .startWith(ChargeBasisType[entry.type])
      .switchMap(type => this.onTypeChange(type));
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

  private onTypeChange(type: string): Observable<string[]> {
    if (ChargeBasisType.NEGLIGENCE_FEE === ChargeBasisType[type]) {
      return this.textCtrl.valueChanges
        .startWith(undefined)
        .debounceTime(300)
        .map(text => this.filterNegligenceFeeTypes(text));
    } else {
      return Observable.of([]);
    }
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

export interface ChargeBasisEntryModalData {
  entry: ChargeBasisEntry;
}
