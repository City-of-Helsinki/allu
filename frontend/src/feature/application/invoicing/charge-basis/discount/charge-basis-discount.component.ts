import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {InvoiceHub} from '../../../../../service/application/invoice/invoice-hub';
import {Observable} from 'rxjs/Observable';
import {ChargeBasisEntry} from '../../../../../model/application/invoice/charge-basis-entry';
import {Subject} from 'rxjs/Subject';
import {ChargeBasisUnit} from '../../../../../model/application/invoice/charge-basis-unit';

const discountSumValidators = {
  unitPrice: [Validators.required]
};

const discountPercentValidators = {
  unitPrice: undefined
};

@Component({
  selector: 'charge-basis-discount',
  template: require('./charge-basis-discount.component.html'),
  styles: [
    require('./charge-basis-discount.component.scss')
  ]
})
export class ChargeBasisDiscountComponent implements OnInit, OnDestroy {

  @Input() form: FormGroup;

  unitTypes = [ChargeBasisUnit.PIECE, ChargeBasisUnit.PERCENT].map(type => ChargeBasisUnit[type]);
  referableEntries: Observable<Array<ChargeBasisEntry>>;
  unitCtrl: FormControl;

  private destroy = new Subject<boolean>();

  constructor(private invoiceHub: InvoiceHub) {}

  ngOnInit(): void {
    this.referableEntries = this.invoiceHub.chargeBasisEntries
      .map(entries => entries.filter(entry => !!entry.tag));

    this.unitCtrl = <FormControl>this.form.get('unit');
    this.unitCtrl.valueChanges
      .takeUntil(this.destroy)
      .subscribe(unit => this.unitChanges(unit));

    this.unitCtrl.updateValueAndValidity();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  private unitChanges(unitName: string): void {
    const unit = ChargeBasisUnit[unitName];
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
