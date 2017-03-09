import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
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

  constructor(private fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    let areaRental = <AreaRental>this.application.extension || new AreaRental();
    this.applicationForm.patchValue(AreaRentalForm.from(this.application, areaRental));
  }

  protected update(form: AreaRentalForm): Application {
    let application = this.application;
    application.name = 'Aluevuokraus'; // Area rentals have no name so set default
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = AreaRentalForm.to(form);

    application.location.startTime = application.startTime;
    application.location.endTime = application.endTime;

    return application;
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['', Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      workFinished: [''],
      trafficArrangements: [''],
      additionalInfo: ['']
    });
  }
}
