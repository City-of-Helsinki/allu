import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {EventNature} from '../../../../model/application/event/event-nature';
import {EnumUtil} from '../../../../util/enum.util';
import {findTranslation} from '../../../../util/translations';
import {NotBillableReason} from '../../../../model/application/not-billable-reason';
import {Observable} from 'rxjs/Observable';
import {Some} from '../../../../util/option';
import {ApplicationKind} from '../../../../model/application/type/application-kind';

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
  notBillableReasons = EnumUtil.enumValues(NotBillableReason)
    .map(reason => findTranslation(['application.event.notBillableReason', reason]));
  matchingReasons: Observable<Array<string>>;

  private notBillableCtrl: FormControl;
  private notBillableReasonCtrl: FormControl;

  ngOnInit(): void {
    if (ApplicationKind.OUTDOOREVENT === ApplicationKind[this.kind]) {
      this.notBillableCtrl = <FormControl>this.form.get('notBillable');
      this.notBillableReasonCtrl = <FormControl>this.form.get('notBillableReason');
      this.notBillableCtrl.valueChanges.subscribe(notBillable => this.notBillableChange(notBillable));

      this.matchingReasons = this.notBillableReasonCtrl.valueChanges
        .startWith(undefined)
        .map(reason => this.filterReasons(reason));
    }
  }

  eventNatureChange(nature: string): void {
    if (EventNature.PUBLIC_FREE !== EventNature[nature]) {
      this.form.patchValue({notBillable: false});
      this.notBillableChange(true);
    }
  }

  notBillableChange(notBillable: boolean): void {
    if (notBillable) {
      this.form.patchValue({
        salesActivity: false,
        heavyStructure: false,
        notBillableReason: undefined
      });
    }
  }

  private filterReasons(value: string): string[] {
    return Some(value)
      .map(val => this.notBillableReasons
        .filter(reason => reason.toUpperCase().indexOf(val.toUpperCase()) === 0))
      .orElse(this.notBillableReasons.slice());
  }
}
