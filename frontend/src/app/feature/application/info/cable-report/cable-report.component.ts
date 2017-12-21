import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ComplexValidator} from '../../../../util/complex-validator';
import {CableReportForm, OrdererIdForm} from './cable-report.form';
import {ApplicationStore} from '../../../../service/application/application-store';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {CableReport} from '../../../../model/application/cable-report/cable-report';
import {ApplicationStatus} from '../../../../model/application/application-status';
import {ProjectHub} from '../../../../service/project/project-hub';
import {TimeUtil} from '../../../../util/time.util';

@Component({
  selector: 'cable-report',
  viewProviders: [],
  templateUrl: './cable-report.component.html',
  styleUrls: []
})
export class CableReportComponent extends ApplicationInfoBaseComponent implements OnInit {

  showCableInfo = false;

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    applicationStore: ApplicationStore,
    router: Router,
    projectHub: ProjectHub) {
    super(fb, route, applicationStore, router, projectHub);
  }

  ngOnInit(): any {
    super.ngOnInit();

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
      workDescription: [''],
      ordererId: [OrdererIdForm.createDefault(), Validators.required]
    });
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    this.applicationForm.patchValue(CableReportForm.from(application));
    this.showCableInfo = ApplicationStatus[application.status] >= ApplicationStatus.HANDLING;
  }

  protected update(form: CableReportForm): Application {
    const application = super.update(form);
    application.name = 'Johtoselvitys'; // Cable reports have no name so set default
    application.startTime = TimeUtil.toStartDate(form.reportTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.reportTimes.endTime);
    const extension = <CableReport>application.extension;
    application.extension = CableReportForm.to(form, extension.validityTime);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }
}
