import {Component, OnInit} from '@angular/core';
import {Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {AreaRental} from '../../../../model/application/area-rental/area-rental';
import {AreaRentalForm} from './area-rental.form';
import {TimeUtil} from '../../../../util/time.util';

@Component({
  selector: 'area-rental',
  viewProviders: [],
  templateUrl: './area-rental.component.html',
  styleUrls: []
})
export class AreaRentalComponent extends ApplicationInfoBaseComponent implements OnInit {

  protected initForm() {
    this.applicationForm = this.fb.group({
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
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const areaRental = <AreaRental>application.extension || new AreaRental();
    this.applicationForm.patchValue(AreaRentalForm.from(application, areaRental));
  }

  protected update(form: AreaRentalForm): Application {
    const application = super.update(form);
    application.name = 'Aluevuokraus'; // Area rentals have no name so set default
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = AreaRentalForm.to(form);

    application.firstLocation.startTime = application.startTime;
    application.firstLocation.endTime = application.endTime;

    return application;
  }
}
