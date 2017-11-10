import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {AreaRental} from '../../../../model/application/area-rental/area-rental';
import {AreaRentalForm} from './area-rental.form';


@Component({
  selector: 'area-rental',
  viewProviders: [],
  template: require('./area-rental.component.html'),
  styles: []
})
export class AreaRentalComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(fb: FormBuilder, route: ActivatedRoute, applicationState: ApplicationState) {
    super(fb, route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    let areaRental = <AreaRental>this.application.extension || new AreaRental();
    this.applicationForm.patchValue(AreaRentalForm.from(this.application, areaRental));
  }

  protected update(form: AreaRentalForm): Application {
    let application = super.update(form);
    application.name = 'Aluevuokraus'; // Area rentals have no name so set default
    application.startTime = form.validityTimes.startTime;
    application.endTime = form.validityTimes.endTime;
    application.extension = AreaRentalForm.to(form);

    application.firstLocation.startTime = application.startTime;
    application.firstLocation.endTime = application.endTime;

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
