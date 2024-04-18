import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormControl, UntypedFormGroup} from '@angular/forms';
import {Subscription} from 'rxjs';
import {EventNature, selectableNatures} from '@model/application/event/event-nature';
import {FormUtil} from '@util/form.util';
import {ApplicationKind} from '@model/application/type/application-kind';
import {Application, hasFixedLocations} from '@model/application/application';
import {ApplicationType} from '@model/application/type/application-type';

@Component({
  selector: 'pricing-info',
  viewProviders: [],
  templateUrl: './pricing-info.component.html',
  styleUrls: []
})
export class PricingInfoComponent implements OnInit, OnDestroy {

  @Input() form: UntypedFormGroup;
  @Input() application: Application;

  @Output() billableChange = new EventEmitter<boolean>();

  showPricingInfo = false;
  eventNatures = selectableNatures.map(nature => EventNature[nature]);
  required = FormUtil.required;
  billableSalesAreaSubscription: Subscription;
  natureSelectable: boolean;
  surfaceHardnessSelectable: boolean;
  ecoCompassSelectable: boolean;
  distanceFromWallSelectable: boolean;
  distanceFromWallSelectedDefault: boolean;

  private billableSalesAreaControl: UntypedFormControl;

  ngOnInit(): void {
    this.billableSalesAreaControl = <UntypedFormControl>this.form.get('billableSalesArea');
    if (this.billableSalesAreaControl) {
      this.billableSalesAreaSubscription = this.billableSalesAreaControl.valueChanges
        .subscribe(billable => this.billableChange.emit(billable));
    }

    this.natureSelectable = this.application.kind === ApplicationKind.OUTDOOREVENT;
    this.surfaceHardnessSelectable = this.application.type === ApplicationType.EVENT && !hasFixedLocations(this.application);

    this.ecoCompassSelectable = this.application.kind === ApplicationKind.OUTDOOREVENT
      || this.application.kind === ApplicationKind.BIG_EVENT;

    this.distanceFromWallSelectable = [
      ApplicationKind.PROMOTION_OR_SALES,
      ApplicationKind.SUMMER_TERRACE,
      ApplicationKind.WINTER_TERRACE
    ].indexOf(this.application.kind) >= 0;

    this.distanceFromWallSelectedDefault = [
      ApplicationKind.SUMMER_TERRACE,
      ApplicationKind.WINTER_TERRACE
    ].indexOf(this.application.kind) >= 0;

    this.showPricingInfo = this.natureSelectable
      || this.surfaceHardnessSelectable
      || this.ecoCompassSelectable
      || this.distanceFromWallSelectable;
  }

  ngOnDestroy(): void {
    if (this.billableSalesAreaSubscription) {
      this.billableSalesAreaSubscription.unsubscribe();
    }
  }
}
