import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {EventNature} from '../../../../model/application/event/event-nature';
import {EnumUtil} from '../../../../util/enum.util';
import {NoPriceReason} from '../../../../model/application/no-price-reason';

@Component({
  selector: 'pricing-info',
  viewProviders: [],
  template: require('./pricing-info.component.html'),
  styles: []
})
export class PricingInfoComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() kind: string;

  eventNatures = EnumUtil.enumValues(EventNature).filter(nature => nature !== 'PROMOTION');
  noPriceReasons = EnumUtil.enumValues(NoPriceReason);

  ngOnInit(): void {
  }

  eventNatureChange(nature: string): void {
    if (EventNature.PUBLIC_FREE !== EventNature[nature]) {
      this.form.patchValue({noPrice: false});
      this.noPriceChange(true);
    }
  }

  noPriceChange(noPrice: boolean): void {
    if (noPrice) {
      this.form.patchValue({
        salesActivity: false,
        heavyStructure: false,
        noPriceReason: undefined
      });
    }
  }
}
