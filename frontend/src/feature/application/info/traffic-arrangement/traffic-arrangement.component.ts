import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';
import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationState} from '../../../../service/application/application-state';
import {TrafficArrangement} from '../../../../model/application/traffic-arrangement/traffic-arrangement';
import {TrafficArrangementForm} from './traffic-arrangement.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';


@Component({
  selector: 'traffic-arrangement',
  viewProviders: [],
  template: require('./traffic-arrangement.component.html'),
  styles: []
})
export class TrafficArrangementComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(private fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    let arrangement = <TrafficArrangement>this.application.extension || new TrafficArrangement();
    this.applicationForm.patchValue(TrafficArrangementForm.from(this.application, arrangement));
  }

  protected update(form: TrafficArrangementForm): Application {
    let application = super.update(form);
    application.name = 'Liikennejärjestely'; // Traffic arrangements have no name so set default
    application.startTime = form.validityTimes.startTime;
    application.endTime = form.validityTimes.endTime;
    application.extension = TrafficArrangementForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      pksCard: [false],
      workFinished: [undefined],
      calculatedPrice: [0],
      priceOverride: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      priceOverrideReason: [''],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      additionalInfo: ['']
    });
  }
}
