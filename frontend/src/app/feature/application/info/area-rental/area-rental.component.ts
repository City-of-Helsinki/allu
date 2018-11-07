import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';

import {Application} from '@model/application/application';
import {ApplicationInfoBaseComponent} from '@feature/application/info/application-info-base.component';
import {AreaRental} from '@model/application/area-rental/area-rental';
import {AreaRentalForm, from, to} from './area-rental.form';
import {TimeUtil} from '@util/time.util';

@Component({
  selector: 'area-rental',
  viewProviders: [],
  templateUrl: './area-rental.component.html',
  styleUrls: []
})
export class AreaRentalComponent extends ApplicationInfoBaseComponent implements OnInit {
  protected createExtensionForm(): FormGroup {
    return this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }),
      pksCard: [false],
      workFinished: [undefined],
      calculatedPrice: [0],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      additionalInfo: [''],
      terms: [undefined]
    });
  }

  get workFinished(): Date {
    return this.applicationForm.getRawValue().workFinished;
  }

  get startTime(): Date {
    return this.applicationForm.get('validityTimes.startTime').value;
  }

  get endTime(): Date {
    return this.applicationForm.get('validityTimes.endTime').value;
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const areaRental = <AreaRental>application.extension || new AreaRental();
    this.applicationForm.patchValue(from(application, areaRental));
  }

  protected update(form: AreaRentalForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = to(form);
    return application;
  }
}
