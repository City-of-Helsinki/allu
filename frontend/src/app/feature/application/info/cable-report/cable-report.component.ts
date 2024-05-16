import {Component, OnInit} from '@angular/core';
import {UntypedFormGroup, Validators} from '@angular/forms';

import {Application} from '@model/application/application';
import {ApplicationTag} from '@model/application/tag/application-tag';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';
import {ComplexValidator} from '@util/complex-validator';
import {CableReportForm, createDefaultOrdererId, from, to} from './cable-report.form';
import {ApplicationInfoBaseComponent} from '@feature/application/info/application-info-base.component';
import {CableReport} from '@model/application/cable-report/cable-report';
import {ApplicationStatus, isSameOrAfter} from '@model/application/application-status';
import * as fromApplication from '@feature/application/reducers';
import {Add, Remove} from '@feature/application/actions/application-tag-actions';
import {select} from '@ngrx/store';
import {Observable} from 'rxjs/index';
import {TimeUtil} from '@util/time.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {createTranslated} from '@service/error/error-info';
import {FormUtil} from '@util/form.util';

@Component({
  selector: 'cable-report',
  viewProviders: [],
  templateUrl: './cable-report.component.html',
  styleUrls: []
})
export class CableReportComponent extends ApplicationInfoBaseComponent implements OnInit {

  showCableInfo = false;
  isSurveyRequired: Observable<boolean>;

  ngOnInit(): void {
    super.ngOnInit();
    this.isSurveyRequired = this.store.pipe(select(fromApplication.hasTag(ApplicationTagType.SURVEY_REQUIRED)));
  }

  protected createExtensionForm(): UntypedFormGroup {
    return this.fb.group({
      validityTime: [{value: undefined, disabled: true}],
      constructionWork: [{value: false, disabled: this.readonly}],
      maintenanceWork: [{value: false, disabled: this.readonly}],
      emergencyWork: [{value: false, disabled: this.readonly}],
      propertyConnectivity: [{value: false, disabled: this.readonly}],
      reportTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      workDescription: [''],
      ordererId: [undefined, Validators.required]
    });
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    this.applicationForm.patchValue(from(application));
    this.showCableInfo = isSameOrAfter(application.status, ApplicationStatus.HANDLING);
  }

  protected update(form: CableReportForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.reportTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.reportTimes.endTime);
    const extension = <CableReport>application.extension;
    application.extension = to(form, extension.validityTime);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected onValidationErrors(): void {
    const ordererMissing = this.applicationForm.hasError('required', ['ordererId']);
    const errorCount = FormUtil.errorCount(this.applicationForm);
    const ordererMissingError = createTranslated('common.field.faultyValueTitle', 'application.cableReport.field.ordererMissing');
    const basicError = createTranslated('common.field.faultyValueTitle', 'common.field.faultyValue');

    if (ordererMissing) {
      this.store.dispatch(new NotifyFailure(ordererMissingError));

      if (errorCount > 1) {
        this.store.dispatch(new NotifyFailure(basicError));
      }
    } else {
      this.store.dispatch(new NotifyFailure(basicError));
    }
  }

  markSurveyRequired(): void {
    this.store.dispatch(new Add(new ApplicationTag(ApplicationTagType.SURVEY_REQUIRED, undefined, new Date())));
  }

  markSurveyDone(): void {
    this.store.dispatch(new Remove(ApplicationTagType.SURVEY_REQUIRED));
  }
}
