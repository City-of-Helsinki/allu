import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormControl} from '@angular/forms';
import {Subscription} from 'rxjs/internal/Subscription';
import {InvoicingPeriodLength} from '@feature/application/invoicing/invoicing-period/invoicing-period-length';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Change, Remove} from '@feature/application/invoicing/actions/invoicing-period-actions';

@Component({
  selector: 'invoicing-period-select',
  templateUrl: './invoicing-period-select.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoicingPeriodSelectComponent implements OnInit, OnDestroy {
  @Input() selectedPeriod: InvoicingPeriodLength;
  @Input() disabled: boolean;

  periods: InvoicingPeriodLength[] = [1, 3, 6, 12];

  periodCtrl: UntypedFormControl;

  private periodSub: Subscription;

  constructor(private fb: UntypedFormBuilder,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.periodCtrl = this.fb.control({value: this.selectedPeriod, disabled: this.disabled});
    this.periodSub = this.periodCtrl.valueChanges
      .subscribe(period => this.onPeriodChange(period));
  }

  ngOnDestroy(): void {
    this.periodSub.unsubscribe();
  }

  private onPeriodChange(period: InvoicingPeriodLength): void {
    if (period) {
      this.store.dispatch(new Change(period));
    } else {
      this.store.dispatch(new Remove());
    }
  }
}
