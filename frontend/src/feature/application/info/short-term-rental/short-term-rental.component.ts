import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ApplicantForm} from '../applicant/applicant.form';
import {ShortTermRentalForm} from './short-term-rental.form';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ShortTermRental} from '../../../../model/application/short-term-rental/short-term-rental';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';

@Component({
  selector: 'short-term-rental',
  viewProviders: [],
  template: require('./short-term-rental.component.html'),
  styles: []
})
export class ShortTermRentalComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(private fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    let rental = <ShortTermRental>this.application.extension || new ShortTermRental();
    this.applicationForm.patchValue(ShortTermRentalForm.from(this.application, rental));
  }

  protected update(form: ShortTermRentalForm): Application {
    let application = super.update(form);
    application.name = form.name;
    application.startTime = form.rentalTimes.startTime;
    application.endTime = form.rentalTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = ShortTermRentalForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }


  protected initForm() {
    this.applicationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: ['', Validators.required],
      area: undefined,
      commercial: [false],
      largeSalesArea: [false],
      calculatedPrice: [0],
      priceOverride: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      priceOverrideReason: [''],
      rentalTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime'))
    });
  }
}
