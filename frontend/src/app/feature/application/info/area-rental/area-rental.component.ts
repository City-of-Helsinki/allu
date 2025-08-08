import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';

import {Application} from '@model/application/application';
import {ApplicationInfoBaseComponent} from '@feature/application/info/application-info-base.component';
import {AreaRental} from '@model/application/area-rental/area-rental';
import {AreaRentalForm, from, to} from './area-rental.form';
import {TimeUtil} from '@util/time.util';
import {ApplicationStatus} from '@model/application/application-status';
import {
  DATE_REPORTING_MODAL_CONFIG,
  DateReportingModalComponent,
  DateReportingModalData,
  ReportedDateType,
  ReporterType
} from '@feature/application/date-reporting/date-reporting-modal.component';
import {ReportCustomerWorkFinished} from '@feature/application/actions/date-reporting-actions';
import {Observable} from 'rxjs';
import {DateReport} from '@model/application/date-report';
import {filter} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationService} from '@service/application/application.service';
import {NotificationService} from '@feature/notification/notification.service';
import {ProjectService} from '@service/project/project.service';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'area-rental',
  viewProviders: [],
  templateUrl: './area-rental.component.html',
  styleUrls: []
})
export class AreaRentalComponent extends ApplicationInfoBaseComponent implements OnInit {
  showReportCustomerDates = false;

  constructor(fb: UntypedFormBuilder,
              route: ActivatedRoute,
              applicationStore: ApplicationStore,
              applicationService: ApplicationService,
              notification: NotificationService,
              router: Router,
              projectService: ProjectService,
              store: Store<fromRoot.State>,
              private dialog: MatDialog) {
    super(fb, route, applicationStore, applicationService, notification, router, projectService, store);
  }

  protected createExtensionForm(): UntypedFormGroup {
    return this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }),
      pksCard: [false],
      majorDisturbance: [false],
      workFinished: [undefined],
      customerWorkFinished: [undefined],
      calculatedPrice: [0],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      workPurpose: ['', Validators.required],
      additionalInfo: [''],
      terms: [undefined]
    });
  }

  get workFinished(): Date {
    return this.applicationForm.getRawValue().workFinished;
  }

  get startTime(): Date {
    return this.applicationForm.get('validityTimes.startTime').value;
  }

  get endTime(): Date {
    return this.applicationForm.get('validityTimes.endTime').value;
  }

  reportCustomerWorkFinished(areaRental: AreaRental): void {
    const data: DateReportingModalData = {
      reporterType: ReporterType.CUSTOMER,
      dateType: ReportedDateType.WORK_FINISHED,
      reportedDate: areaRental.customerWorkFinished,
      reportingDate: areaRental.workFinishedReported
    };
    this.openDateReporting(data).subscribe(dateReport => this.store.dispatch(new ReportCustomerWorkFinished(dateReport)));
  }

  private openDateReporting(data: DateReportingModalData): Observable<DateReport> {
    return this.dialog.open(DateReportingModalComponent, {
      ...DATE_REPORTING_MODAL_CONFIG,
      data
    }).afterClosed().pipe(
      filter(result => !!result)
    );
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const areaRental = <AreaRental>application.extension || new AreaRental();
    this.applicationForm.patchValue(from(application, areaRental));
    this.showReportCustomerDates = ApplicationStatus.DECISION === application.status;
  }

  protected update(form: AreaRentalForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = to(form);
    return application;
  }
}
