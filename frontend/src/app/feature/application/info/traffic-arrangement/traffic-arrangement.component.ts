import {Component, OnInit} from '@angular/core';
import {Validators} from '@angular/forms';
import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {TrafficArrangement} from '../../../../model/application/traffic-arrangement/traffic-arrangement';
import {TrafficArrangementForm} from './traffic-arrangement.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {TimeUtil} from '../../../../util/time.util';
import {ApplicationStatus, isSameOrAfter} from '../../../../model/application/application-status';

@Component({
  selector: 'traffic-arrangement',
  viewProviders: [],
  templateUrl: './traffic-arrangement.component.html',
  styleUrls: []
})
export class TrafficArrangementComponent extends ApplicationInfoBaseComponent implements OnInit {

  showImpedimentType = false;

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      calculatedPrice: [0],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      workPurpose: ['']
    });
  }

  protected update(form: TrafficArrangementForm): Application {
    const application = super.update(form);
    application.name = 'Liikennejärjestely'; // Traffic arrangements have no name so set default
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = TrafficArrangementForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const arrangement = <TrafficArrangement>application.extension || new TrafficArrangement();
    this.applicationForm.patchValue(TrafficArrangementForm.from(application, arrangement));

    this.showImpedimentType = isSameOrAfter(application.status, ApplicationStatus.HANDLING);
  }
}
