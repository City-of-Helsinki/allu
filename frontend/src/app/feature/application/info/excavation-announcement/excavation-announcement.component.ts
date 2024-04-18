import {Component, OnInit} from '@angular/core';
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {filter, take} from 'rxjs/internal/operators';
import {MatDatepicker} from '@angular/material/datepicker';
import {MatDialog} from '@angular/material/dialog';
import {Application} from '@model/application/application';
import {AbstractControlWarn, ComplexValidator} from '@util/complex-validator';
import {ExcavationAnnouncementForm, from, to} from './excavation-announcement.form';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {TimeUtil} from '@util/time.util';
import {Some} from '@util/option';
import {IconConfig} from '@feature/common/icon-config';
import {ConfigurationKey} from '@model/config/configuration-key';
import {NotificationService} from '@feature/notification/notification.service';
import * as fromRoot from '@feature/allu/reducers';
import {ActivatedRoute, Router} from '@angular/router';
import {ApplicationService} from '@service/application/application.service';
import {ProjectService} from '@service/project/project.service';
import {ApplicationStore} from '@service/application/application-store';
import {Store} from '@ngrx/store';
import {
  DATE_REPORTING_MODAL_CONFIG,
  DateReportingModalComponent,
  DateReportingModalData,
  ReportedDateType,
  ReporterType
} from '@feature/application/date-reporting/date-reporting-modal.component';
import {ApplicationStatus} from '@model/application/application-status';
import {DateReport} from '@model/application/date-report';
import {
  ReportCustomerOperationalCondition,
  ReportCustomerValidity,
  ReportCustomerWorkFinished
} from '@feature/application/actions/date-reporting-actions';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {ArrayUtil} from '@util/array-util';

@Component({
  selector: 'excavation-announcement',
  viewProviders: [],
  templateUrl: './excavation-announcement.component.html',
  styleUrls: []
})
export class ExcavationAnnouncementComponent extends ApplicationInfoBaseComponent implements OnInit {
  validityEndTimeCtrl: AbstractControlWarn;
  validityEndTimeIcon: IconConfig = new IconConfig(undefined, true, 'today');
  showReportCustomerDates = false;

  private winterTimeOperationCtrl: AbstractControl;
  private winterTimeStart: string;
  public winterTimeEnd: string;

  constructor(fb: UntypedFormBuilder,
              route: ActivatedRoute,
              applicationStore: ApplicationStore,
              applicationService: ApplicationService,
              notification: NotificationService,
              router: Router,
              projectService: ProjectService,
              store: Store<fromRoot.State>,
              private dialog: MatDialog,
              private configurationHelper: ConfigurationHelperService) {
    super(fb, route, applicationStore, applicationService, notification, router, projectService, store);
  }

  onValidityEndTimePickerClick(picker: MatDatepicker<Date>): void {
    if (this.validityEndTimeCtrl.warnings.inWinterTime) {
      Some(this.validityEndTimeCtrl.value).do(date => {
        // Clear errors and disable validators temporarily because otherwise if end date is same as
        // winter end date the control would immediately become invalid again.
        this.validityEndTimeCtrl.clearValidators();
        this.validityEndTimeCtrl.warnings = [];
        this.validityEndTimeCtrl.patchValue(TimeUtil.toTimePeriodEnd(date, TimeUtil.dateFromBackend(this.winterTimeEnd)));
        this.setEndTimeCtrlValidators();

        this.winterTimeOperationCtrl.patchValue(date);
      });
    } else {
      picker.open();
    }
  }

  reportCustomerValidity(excavation: ExcavationAnnouncement): void {
    const data: DateReportingModalData = {
      reporterType: ReporterType.CUSTOMER,
      dateType: ReportedDateType.VALIDITY,
      reportedDate: excavation.customerStartTime,
      reportedEndDate: excavation.customerEndTime,
      reportingDate: excavation.validityReported
    };
    this.openDateReporting(data).subscribe(dateReport => this.store.dispatch(new ReportCustomerValidity(dateReport)));
  }

  reportCustomerOperationalCondition(excavation: ExcavationAnnouncement): void {
    const data: DateReportingModalData = {
      reporterType: ReporterType.CUSTOMER,
      dateType: ReportedDateType.WINTER_TIME_OPERATION,
      reportedDate: excavation.customerWinterTimeOperation,
      reportingDate: excavation.operationalConditionReported
    };
    this.openDateReporting(data).subscribe(dateReport => this.store.dispatch(new ReportCustomerOperationalCondition(dateReport)));
  }

  reportCustomerWorkFinished(excavation: ExcavationAnnouncement): void {
    const data: DateReportingModalData = {
      reporterType: ReporterType.CUSTOMER,
      dateType: ReportedDateType.WORK_FINISHED,
      reportedDate: excavation.customerWorkFinished,
      reportingDate: excavation.workFinishedReported
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

  protected initForm() {
    super.initForm();

    this.validityEndTimeCtrl = <AbstractControlWarn>this.applicationForm.get(['validityTimes', 'endTime']);
    this.validityEndTimeCtrl.statusChanges.subscribe(status => this.onValidityEndTimeChange(status));

    if (this.applicationStore.isNew) {
      this.validityEndTimeCtrl.markAsDirty(); // To trigger validation
    }

    this.winterTimeOperationCtrl = this.applicationForm.controls['winterTimeOperation'];

    combineLatest(
      this.configurationHelper.getSingleConfiguration(ConfigurationKey.WINTER_TIME_START),
      this.configurationHelper.getSingleConfiguration(ConfigurationKey.WINTER_TIME_END))
        .pipe(take(1)).subscribe(([start, end]) => {
          this.winterTimeStart = start.value;
          this.winterTimeEnd = end.value;
          this.setEndTimeCtrlValidators();
        });
  }

  protected createExtensionForm(): UntypedFormGroup {
    return this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, [Validators.required]]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      pksCard: [false],
      constructionWork: [{value: false, disabled: this.readonly}],
      maintenanceWork: [{value: false, disabled: this.readonly}],
      emergencyWork: [{value: false, disabled: this.readonly}],
      propertyConnectivity: [{value: false, disabled: this.readonly}],
      selfSupervision: [{value: false, disabled: this.readonly}],
      winterTimeOperation: [undefined],
      workFinished: [undefined],
      unauthorizedWork: this.fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      guaranteeEndTime: [undefined],
      customerValidityTimes: this.fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      customerWinterTimeOperation: [undefined],
      customerWorkFinished: [undefined],
      calculatedPrice: [0],
      workPurpose: ['', Validators.required],
      additionalInfo: [''],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required],
      compactionAndBearingCapacityMeasurement: [false],
      qualityAssuranceTest: [false],
      terms: [undefined],
      cableReports: [[]],
      placementContracts: [[]],
    });
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const excavation = <ExcavationAnnouncement>application.extension || new ExcavationAnnouncement();
    const form = from(application, excavation);
    this.applicationForm.patchValue(form);
    this.showReportCustomerDates = ArrayUtil.contains(
        [ApplicationStatus.DECISION, ApplicationStatus.OPERATIONAL_CONDITION], application.status);

    // Run validation for validty end time so that if it is on winter time an icon will be shown
    this.validityEndTimeCtrl.updateValueAndValidity();
  }

  protected update(form: ExcavationAnnouncementForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = to(form, <ExcavationAnnouncement>application.extension);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  private onValidityEndTimeChange(status: any) {
    if (this.validityEndTimeCtrl.warnings) {
      this.validityEndTimeIcon = this.validityEndTimeCtrl.warnings.inWinterTime
        ? new IconConfig('accent', false, 'warning')
        : new IconConfig(undefined, true, 'today');
    }
  }

  private setEndTimeCtrlValidators(): void {
    const start = TimeUtil.dateFromBackend(this.winterTimeStart);
    const end = TimeUtil.dateFromBackend(this.winterTimeEnd);
    this.validityEndTimeCtrl.setValidators(
        [Validators.required, ComplexValidator.inWinterTimeWarn(start, end)]);
  }
}
