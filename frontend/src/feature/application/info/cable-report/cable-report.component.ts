import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {CableReportForm} from './cable-report.form';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {CableReport} from '../../../../model/application/cable-report/cable-report';

@Component({
  selector: 'cable-report',
  viewProviders: [],
  template: require('./cable-report.component.html'),
  styles: []
})
export class CableReportComponent extends ApplicationInfoBaseComponent implements OnInit {
  constructor(fb: FormBuilder, route: ActivatedRoute, applicationState: ApplicationState) {
    super(fb, route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    this.applicationForm.patchValue(CableReportForm.from(this.application));
  }


  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTime: [{value: undefined, disabled: true}],
      cableSurveyRequired: [false],
      mapUpdated: [false],
      constructionWork: [{value: false, disabled: this.readonly}],
      maintenanceWork: [{value: false, disabled: this.readonly}],
      emergencyWork: [{value: false, disabled: this.readonly}],
      propertyConnectivity: [{value: false, disabled: this.readonly}],
      reportTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      workDescription: ['']
    });
  }

  protected update(form: CableReportForm): Application {
    let application = super.update(form);
    application.name = 'Johtoselvitys'; // Cable reports have no name so set default
    application.startTime = form.reportTimes.startTime;
    application.endTime = form.reportTimes.endTime;
    let extension = <CableReport>application.extension;
    application.extension = CableReportForm.to(form, extension.validityTime, extension.specifiers);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }
}
