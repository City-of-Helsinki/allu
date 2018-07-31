import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormGroup, FormControl} from '@angular/forms';
import {Subscription} from 'rxjs';
import {EventNature} from '../../../../model/application/event/event-nature';
import {EnumUtil} from '../../../../util/enum.util';
import {FormUtil} from '../../../../util/form.util';

@Component({
  selector: 'pricing-info',
  viewProviders: [],
  templateUrl: './pricing-info.component.html',
  styleUrls: []
})
export class PricingInfoComponent implements OnInit, OnDestroy {

  @Input() form: FormGroup;
  @Input() kind: string;

  @Output() billableChange = new EventEmitter<boolean>();

  eventNatures = EnumUtil.enumValues(EventNature).filter(nature => nature !== 'PROMOTION');
  required = FormUtil.required;
  billableSalesAreaSubscription: Subscription;

  private billableSalesAreaControl: FormControl;

  ngOnInit(): void {
    this.billableSalesAreaControl = <FormControl>this.form.get('billableSalesArea');
    if (this.billableSalesAreaControl) {
      this.billableSalesAreaSubscription = this.billableSalesAreaControl.valueChanges
        .subscribe(billable => this.billableChange.emit(billable));
    }
  }

  ngOnDestroy(): void {
    if (this.billableSalesAreaSubscription) {
      this.billableSalesAreaSubscription.unsubscribe();
    }
  }
}
