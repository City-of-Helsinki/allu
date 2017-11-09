import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import {Some} from '../../../../../util/option';
import {EnumUtil} from '../../../../../util/enum.util';
import {NegligenceFeeType} from '../../../../../model/application/invoice/negligence-fee-type';
import {findTranslation} from '../../../../../util/translations';
import {ChargeBasisUnit} from '../../../../../model/application/invoice/charge-basis-unit';

@Component({
  selector: 'charge-basis-negligence-fee',
  template: require('./charge-basis-negligence-fee.component.html'),
  styles: []
})
export class ChargeBasisNegligenceFeeComponent implements OnInit, OnDestroy {

  @Input() form: FormGroup;

  negligenceFeeTypes = EnumUtil.enumValues(NegligenceFeeType).map(t => findTranslation(['invoice.negligenceFeeType', t]));
  textCtrl: FormControl;
  matchingTexts: Observable<Array<string>>;
  unitTypes = EnumUtil.enumValues(ChargeBasisUnit);

  private destroy = new Subject<boolean>();

  ngOnInit(): void {
    this.textCtrl = <FormControl>this.form.get('text');

    this.matchingTexts = this.textCtrl.valueChanges
      .startWith(undefined)
      .takeUntil(this.destroy)
      .debounceTime(300)
      .map(text => this.filterNegligenceFeeTypes(text));

    if (!this.form.value.unit) {
      this.form.patchValue({unit: ChargeBasisUnit[ChargeBasisUnit.DAY]});
    }
  }

  ngOnDestroy(): void {
  }

  private filterNegligenceFeeTypes(value: string): string[] {
    return Some(value)
      .map(val => this.negligenceFeeTypes
        .filter(type => type.toUpperCase().indexOf(val.toUpperCase()) === 0))
      .orElse(this.negligenceFeeTypes.slice());
  }
}
