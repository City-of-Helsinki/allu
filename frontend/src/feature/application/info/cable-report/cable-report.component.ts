import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router, NavigationStart} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';

import {Application} from '../../../../model/application/application';
import {translations} from '../../../../util/translations';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {UrlUtil} from '../../../../util/url.util';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
import {CableReportForm} from './cable-report.form';
import {ApplicationState} from '../../../../service/application/application-state';


@Component({
  selector: 'cable-report',
  viewProviders: [],
  template: require('./cable-report.component.html'),
  styles: []
})
export class CableReportComponent implements OnInit, OnDestroy {

  path: string;
  application: Application;
  applicationForm: FormGroup;
  submitPending = false;
  translations = translations;
  pickadateParams = PICKADATE_PARAMETERS;
  readonly: boolean;

  private meta: StructureMeta;
  private routeEvents: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private applicationHub: ApplicationHub,
              private applicationState: ApplicationState) {
  };

  ngOnInit(): any {
    this.initForm();

    this.application = this.applicationState.application;
    this.applicationForm.patchValue(CableReportForm.from(this.application));

    this.applicationHub.loadMetaData(this.application.type).subscribe(meta => this.metadataLoaded(meta));

    UrlUtil.urlPathContains(this.route.parent, 'summary')
      .filter(contains => contains)
      .forEach(summary => {
        this.readonly = summary;
        this.applicationForm.disable();
      });

    this.routeEvents = this.router.events
      .filter(event => event instanceof NavigationStart)
      .subscribe(navStart => {
        if (!this.readonly) {
          this.applicationState.application = this.update(this.applicationForm.value);
        }
      });
  }

  ngOnDestroy(): void {
    this.routeEvents.unsubscribe();
  }

  onSubmit(form: CableReportForm) {
    this.submitPending = true;
    let application = this.update(form);


    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  private initForm() {
    this.applicationForm = this.fb.group({
      cableSurveyRequired: [false],
      pksCard: [false],
      constructionWork: [{value: false, disabled: this.readonly}],
      maintenanceWork: [{value: false, disabled: this.readonly}],
      emergencyWork: [{value: false, disabled: this.readonly}],
      propertyConnectivity: [{value: false, disabled: this.readonly}],
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

  private update(form: CableReportForm): Application {
    let application = this.application;
    application.metadata = this.meta;
    application.name = 'Johtoselvitys'; // Cable reports have no name so set default
    application.uiStartTime = form.reportTimes.startTime;
    application.uiEndTime = form.reportTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.company);
    application.contactList = form.orderer;
    application.extension = CableReportForm.to(form, application.extension.specifiers);
    return application;
  }
}
