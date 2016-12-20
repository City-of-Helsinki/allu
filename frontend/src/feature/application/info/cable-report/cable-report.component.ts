import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {translations} from '../../../../util/translations';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {LocationState} from '../../../../service/application/location-state';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {MapHub} from '../../../../service/map-hub';
import {UrlUtil} from '../../../../util/url.util';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
import {CableReportForm} from './cable-report.form';
import {CableReport} from '../../../../model/application/cable-report/cable-report';


@Component({
  selector: 'cable-report',
  viewProviders: [],
  template: require('./cable-report.component.html'),
  styles: []
})
export class CableReportComponent implements OnInit {

  path: string;
  application: Application;
  applicationForm: FormGroup;
  submitPending = false;
  translations = translations;
  pickadateParams = PICKADATE_PARAMETERS;
  isSummary: boolean;

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

        this.applicationHub.loadMetaData(this.application.type).subscribe(meta => this.metadataLoaded(meta));

        UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
          this.isSummary = summary;
        });

        this.applicationForm.patchValue(CableReportForm.from(application));
      });
  }

  onSubmit(form: CableReportForm) {
    this.submitPending = true;
    let application = this.application;
    application.metadata = this.meta;
    application.name = 'Johtoselvitys'; // Cable reports have no name so set default
    application.uiStartTime = form.reportTimes.startTime;
    application.uiEndTime = form.reportTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.company);
    application.contactList = form.orderer;
    application.extension = CableReportForm.to(form, application.extension.specifiers);

    this.applicationHub.save(application).subscribe(app => {
      this.locationState.clear();
      this.submitPending = false;
      this.router.navigate(['applications', app.id, 'summary']);
    }, err => {
      this.submitPending = false;
    });
  }

  private initForm() {
    this.applicationForm = this.fb.group({
      cableSurveyRequired: [false],
      reportTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['', Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      workDescription: ['']
    });
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
  }
}
