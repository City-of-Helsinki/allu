import {Component, OnDestroy, OnInit, Input, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, Validators} from '@angular/forms';

import {Location} from '../../../model/common/location';
import {Application} from '../../../model/application/application';
import {StructureMeta} from '../../../model/application/structure-meta';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../../util/time.util';
import {LocationState} from '../../../service/application/location-state';
import {ApplicationHub} from '../../../service/application/application-hub';
import {UrlUtil} from '../../../util/url.util';
import {Subscription} from 'rxjs/Subscription';
import {MapHub} from '../../../service/map-hub';
import {ApplicationStatus} from '../../../model/application/application-status-change';
import {ApplicantForm} from '../applicant/applicant.form';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {ShortTermRentalForm} from './short-term-rental.form.ts';
import {ComplexValidator} from '../../../util/complex-validator';
import {translations} from '../../../util/translations';
import {ShortTermRental} from '../../../model/application/short-term-rental/short-term-rental';
import {ShortTermRentalDetailsForm} from './short-term-rental.form';

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
  translations = translations;
  pickadateParams = PICKADATE_PARAMETERS;
  private isSummary: boolean;

  private meta: StructureMeta;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private fb: FormBuilder,
              private locationState: LocationState,
              private applicationHub: ApplicationHub,
              private mapHub: MapHub) {
  };

  ngOnInit(): any {
    this.initForm();

    this.route.parent.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;
        this.application.type = this.route.routeConfig.path;

        this.application.location = this.application.location || this.locationState.location;

        // TODO: mismatch here. Date+time should be used in location too.
        let defaultDate = new Date();
        this.application.startTime = this.locationState.startDate || this.application.startTime || defaultDate;
        this.application.endTime = TimeUtil.getEndOfDay(this.locationState.endDate || this.application.endTime || defaultDate);


        // No metadata available yet
        // this.applicationHub.loadMetaData(this.application.type).subscribe(meta => this.metadataLoaded(meta));

        UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
          this.isSummary = summary;
        });

        let rental = <ShortTermRental>application.event || new ShortTermRental();
        this.rentalForm.patchValue(ShortTermRentalDetailsForm.from(application, rental));
      });
  }

  ngOnDestroy(): any {
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 50);
    this.mapHub.selectApplication(this.application);
  }

  onSubmit(form: ShortTermRentalForm) {
    let application = this.application;
    application.metadata = this.meta;

    application.name = form.details.name;
    application.location.area = form.details.area;
    application.uiStartTime = form.details.rentalTimes.startTime;
    application.uiEndTime = form.details.rentalTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.event = ShortTermRentalDetailsForm.to(form.details);

    this.applicationHub.save(application).subscribe(app => {
      console.log('application saved');
      this.locationState.clear();
      this.router.navigate(['applications', app.id, 'summary']);
    });
  }

  private initForm() {
    this.rentalForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: ['', Validators.required],
      area: undefined,
      rentalTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['', Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime'))
    });

    this.applicationForm = this.fb.group(this.rentalForm);
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
  }
}
