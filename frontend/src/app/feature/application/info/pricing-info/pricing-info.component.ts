import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {Subscription} from 'rxjs';
import {EventNature, selectableNatures} from '@model/application/event/event-nature';
import {EnumUtil} from '@util/enum.util';
import {FormUtil} from '@util/form.util';
import {ApplicationKind} from '@model/application/type/application-kind';

const kindsWithPricingInfo: ApplicationKind[] = [
  ApplicationKind.OUTDOOREVENT,
  ApplicationKind.PROMOTION,
  ApplicationKind.PROMOTION_OR_SALES,
  ApplicationKind.BIG_EVENT
];

@Component({
  selector: 'pricing-info',
  viewProviders: [],
  templateUrl: './pricing-info.component.html',
  styleUrls: []
})
export class PricingInfoComponent implements OnInit, OnDestroy {

  @Input() form: FormGroup;
  @Input() kind: ApplicationKind;

  @Output() billableChange = new EventEmitter<boolean>();

  showPricingInfo = false;
  eventNatures = selectableNatures.map(nature => EventNature[nature]);
  required = FormUtil.required;
  billableSalesAreaSubscription: Subscription;

  private billableSalesAreaControl: FormControl;

  ngOnInit(): void {
    this.billableSalesAreaControl = <FormControl>this.form.get('billableSalesArea');
    if (this.billableSalesAreaControl) {
      this.billableSalesAreaSubscription = this.billableSalesAreaControl.valueChanges
        .subscribe(billable => this.billableChange.emit(billable));
    }

    this.showPricingInfo = kindsWithPricingInfo.indexOf(this.kind) >= 0;
  }

  ngOnDestroy(): void {
    if (this.billableSalesAreaSubscription) {
      this.billableSalesAreaSubscription.unsubscribe();
    }
  }
}
