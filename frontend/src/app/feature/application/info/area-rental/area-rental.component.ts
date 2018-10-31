import {Component, OnInit} from '@angular/core';
import {Validators} from '@angular/forms';

import {Application} from '@model/application/application';
import {ComplexValidator} from '@util/complex-validator';
import {ApplicationInfoBaseComponent} from '@feature/application/info/application-info-base.component';
import {AreaRental} from '@model/application/area-rental/area-rental';
import {AreaRentalForm, from, to} from './area-rental.form';
import {TimeUtil} from '@util/time.util';
import {FormUtil} from '@util/form.util';

@Component({
  selector: 'area-rental',
  viewProviders: [],
  templateUrl: './area-rental.component.html',
  styleUrls: []
})
export class AreaRentalComponent extends ApplicationInfoBaseComponent implements OnInit {

  protected initForm() {
    super.initForm();
    const extensionForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      pksCard: [false],
      workFinished: [undefined],
      calculatedPrice: [0],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      additionalInfo: ['']
    });
    FormUtil.addControls(this.applicationForm, extensionForm.controls);
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

    application.firstLocation.startTime = application.startTime;
    application.firstLocation.endTime = application.endTime;

    return application;
  }
}
