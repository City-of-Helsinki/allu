import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {UrlUtil} from '../../../../util/url.util';
import {MapHub} from '../../../../service/map/map-hub';
import {ApplicantForm} from '../applicant/applicant.form';
import {ShortTermRentalForm} from './short-term-rental.form.ts';
import {ComplexValidator} from '../../../../util/complex-validator';
import {translations} from '../../../../util/translations';
import {ShortTermRental} from '../../../../model/application/short-term-rental/short-term-rental';
import {ShortTermRentalDetailsForm} from './short-term-rental.form';
import {MaterializeUtil} from '../../../../util/materialize.util';
import {ApplicationState} from '../../../../service/application/application-state';

@Component({
  selector: 'short-term-rental',
  viewProviders: [],
  template: require('./short-term-rental.component.html'),
  styles: []
})
export class ShortTermRentalComponent implements OnInit {

  path: string;
  application: Application;
  applicationForm: FormGroup;
  rentalForm: FormGroup;
  submitPending = false;
  translations = translations;
  pickadateParams = PICKADATE_PARAMETERS;
  readonly: boolean;

  private meta: StructureMeta;

  constructor(private route: ActivatedRoute,
              private fb: FormBuilder,
              private applicationHub: ApplicationHub,
              private mapHub: MapHub,
              private applicationState: ApplicationState) {
  };

  ngOnInit(): any {
    this.initForm();

    this.route.parent.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;
        this.application.type = this.route.routeConfig.path;

        this.applicationHub.loadMetaData(this.application.type).subscribe(meta => this.metadataLoaded(meta));

        UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
          this.readonly = summary;
        });

        let rental = <ShortTermRental>application.extension || new ShortTermRental();
        this.rentalForm.patchValue(ShortTermRentalDetailsForm.from(application, rental));

        if (this.readonly) {
          this.applicationForm.disable();
        }
      });
  }

  ngOnDestroy(): any {
  }

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
    this.mapHub.selectApplication(this.application);
  }

  onSubmit(form: ShortTermRentalForm) {
    this.submitPending = true;
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

    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  private initForm() {
    this.applicationForm = this.fb.group({});

    this.rentalForm = this.fb.group({
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

    this.applicationForm.addControl('details', this.rentalForm);
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
  }
}
