import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
import {CableReportForm} from './cable-report.form';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';


@Component({
  selector: 'cable-report',
  viewProviders: [],
  template: require('./cable-report.component.html'),
  styles: []
})
export class CableReportComponent extends ApplicationInfoBaseComponent implements OnInit {
  constructor(private fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    this.applicationForm.patchValue(CableReportForm.from(this.application));
  }


  protected initForm() {
    this.applicationForm = this.fb.group({
      cableSurveyRequired: [false],
      mapUpdated: [false],
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

  protected update(form: CableReportForm): Application {
    let application = super.update(form);
    application.name = 'Johtoselvitys'; // Cable reports have no name so set default
    application.uiStartTime = form.reportTimes.startTime;
    application.uiEndTime = form.reportTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.company);
    application.contactList = form.orderer;
    application.extension = CableReportForm.to(form, application.extension.specifiers);

    application.location.startTime = application.startTime;
    application.location.endTime = application.endTime;

    return application;
  }
}
