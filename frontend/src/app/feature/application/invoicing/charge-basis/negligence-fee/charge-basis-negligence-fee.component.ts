import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormControl, UntypedFormGroup} from '@angular/forms';
import {Subject, Observable} from 'rxjs';
import {Some} from '../../../../../util/option';
import {EnumUtil} from '../../../../../util/enum.util';
import {NegligenceFeeType} from '../../../../../model/application/invoice/negligence-fee-type';
import {findTranslation} from '../../../../../util/translations';
import {ChargeBasisUnit} from '../../../../../model/application/invoice/charge-basis-unit';
import {debounceTime, map, startWith, takeUntil} from 'rxjs/internal/operators';

@Component({
  selector: 'charge-basis-negligence-fee',
  templateUrl: './charge-basis-negligence-fee.component.html',
  styleUrls: []
})
export class ChargeBasisNegligenceFeeComponent implements OnInit, OnDestroy {

  @Input() form: UntypedFormGroup;

  negligenceFeeTypes = EnumUtil.enumValues(NegligenceFeeType).map(t => findTranslation(['invoice.negligenceFeeType', t]));
  textCtrl: UntypedFormControl;
  matchingTexts: Observable<Array<string>>;
  unitTypes = EnumUtil.enumValues(ChargeBasisUnit);

  private destroy = new Subject<boolean>();

  ngOnInit(): void {
    this.textCtrl = <UntypedFormControl>this.form.get('text');

    this.matchingTexts = this.textCtrl.valueChanges.pipe(
      startWith(undefined),
      takeUntil(this.destroy),
      debounceTime(300),
      map(text => this.filterNegligenceFeeTypes(text))
    );

    if (!this.form.value.unit) {
      this.form.patchValue({unit: ChargeBasisUnit.DAY});
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
