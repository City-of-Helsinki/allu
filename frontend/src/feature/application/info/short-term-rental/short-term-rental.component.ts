import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ApplicantForm} from '../applicant/applicant.form';
import {ShortTermRentalForm, ShortTermRentalDetailsForm} from './short-term-rental.form';
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

  private meta: StructureMeta;

  constructor(private applicationHub: ApplicationHub,
              private fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();

    let rental = <ShortTermRental>this.application.extension || new ShortTermRental();
    this.applicationForm.patchValue(ShortTermRentalDetailsForm.from(this.application, rental));

    this.applicationHub.loadMetaData(this.application.type).subscribe(meta => this.metadataLoaded(meta));
  }

  protected update(form: ShortTermRentalForm): Application {
    let application = this.application;
    application.metadata = this.meta;
    application.name = form.details.name;
    application.calculatedPriceEuro = form.details.calculatedPrice;
    application.priceOverrideEuro = form.details.priceOverride;
    application.priceOverrideReason = form.details.priceOverrideReason;
    application.location.area = form.details.area;
    application.uiStartTime = form.details.rentalTimes.startTime;
    application.uiEndTime = form.details.rentalTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = ShortTermRentalDetailsForm.to(form.details);
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
        startTime: ['', Validators.required],
        endTime: ['', Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime'))
    });
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
  }
}
