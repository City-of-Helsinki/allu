import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';
import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationStore} from '../../../../service/application/application-store';
import {TrafficArrangement} from '../../../../model/application/traffic-arrangement/traffic-arrangement';
import {TrafficArrangementForm} from './traffic-arrangement.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';


@Component({
  selector: 'traffic-arrangement',
  viewProviders: [],
  templateUrl: './traffic-arrangement.component.html',
  styleUrls: []
})
export class TrafficArrangementComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(fb: FormBuilder, route: ActivatedRoute, applicationStore: ApplicationStore) {
    super(fb, route, applicationStore);
  }

  ngOnInit(): any {
    super.ngOnInit();
    const arrangement = <TrafficArrangement>this.application.extension || new TrafficArrangement();
    this.applicationForm.patchValue(TrafficArrangementForm.from(this.application, arrangement));
  }

  protected update(form: TrafficArrangementForm): Application {
    const application = super.update(form);
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
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      additionalInfo: ['']
    });
  }
}
