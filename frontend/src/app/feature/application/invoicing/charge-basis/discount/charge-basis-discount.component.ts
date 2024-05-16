import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {Observable, Subject} from 'rxjs';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';
import {ChargeBasisUnit} from '@model/application/invoice/charge-basis-unit';
import {takeUntil} from 'rxjs/internal/operators';
import * as fromInvoicing from '@feature/application/invoicing/reducers';
import {Store} from '@ngrx/store';

const discountSumValidators = {
  unitPrice: [Validators.required]
};

const discountPercentValidators = {
  unitPrice: undefined
};

@Component({
  selector: 'charge-basis-discount',
  templateUrl: './charge-basis-discount.component.html',
  styleUrls: [
    './charge-basis-discount.component.scss'
  ]
})
export class ChargeBasisDiscountComponent implements OnInit, OnDestroy {

  @Input() form: UntypedFormGroup;

  unitTypes = [ChargeBasisUnit.PIECE, ChargeBasisUnit.PERCENT];
  referableEntries: Observable<Array<ChargeBasisEntry>>;
  unitCtrl: UntypedFormControl;

  private destroy = new Subject<boolean>();

  constructor(private store: Store<fromInvoicing.State>) {}

  ngOnInit(): void {
    this.referableEntries = this.store.select(fromInvoicing.getAllReferrableChargeBasisEntries);

    this.unitCtrl = <UntypedFormControl>this.form.get('unit');
    this.unitCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(unit => this.unitChanges(unit));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  private unitChanges(unit: ChargeBasisUnit): void {
    if (ChargeBasisUnit.PERCENT === unit) {
      this.setPercentDefaults();
    } else if (ChargeBasisUnit.PIECE === unit) {
      this.setSumDefaults();
    }
  }

  private setPercentDefaults(): void {
    Object.keys(discountPercentValidators).forEach(key => {
      this.form.get(key).setValidators(discountPercentValidators[key]);
    });
    this.form.patchValue({quantity: undefined, unitPrice: undefined, netPrice: undefined});
  }

  private setSumDefaults(): void {
    Object.keys(discountSumValidators).forEach(key => {
      this.form.get(key).setValidators(discountSumValidators[key]);
    });
    this.form.patchValue({quantity: 1, unitPrice: undefined, netPrice: undefined});
  }
}
