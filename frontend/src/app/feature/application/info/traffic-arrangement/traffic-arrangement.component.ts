import {Component, OnInit} from '@angular/core';
import {UntypedFormGroup, Validators} from '@angular/forms';
import {Application} from '@model/application/application';
import {ComplexValidator} from '@util/complex-validator';
import {TrafficArrangement} from '@model/application/traffic-arrangement/traffic-arrangement';
import {from, to, TrafficArrangementForm} from './traffic-arrangement.form';
import {ApplicationInfoBaseComponent} from '@feature/application/info/application-info-base.component';
import {TimeUtil} from '@util/time.util';
import {ApplicationStatus, isSameOrAfter} from '@model/application/application-status';

@Component({
  selector: 'traffic-arrangement',
  viewProviders: [],
  templateUrl: './traffic-arrangement.component.html',
  styleUrls: []
})
export class TrafficArrangementComponent extends ApplicationInfoBaseComponent implements OnInit {

  protected createExtensionForm(): UntypedFormGroup {
    return this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      calculatedPrice: [0],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      workPurpose: [''],
      terms: [undefined]
    });
  }

  protected update(form: TrafficArrangementForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const arrangement = <TrafficArrangement>application.extension || new TrafficArrangement();
    this.applicationForm.patchValue(from(application, arrangement));
  }
}
