import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, filter, map, switchMap, take} from 'rxjs/internal/operators';
import {MatDatepicker, MatDialog} from '@angular/material';
import {Application} from '@model/application/application';
import {AbstractControlWarn, ComplexValidator} from '@util/complex-validator';
import {ExcavationAnnouncementForm} from './excavation-announcement.form';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {NumberUtil} from '@util/number.util';
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
  ReportedDateType,
  ReporterType
} from '@feature/application/date-reporting/date-reporting-modal.component';
import {ReportCustomerDates} from '@feature/application/actions/excavation-announcement-actions';
import {ApplicationStatus} from '@model/application/application-status';
import {DateReport} from '@feature/application/date-reporting/date-report';

@Component({
  selector: 'excavation-announcement',
  viewProviders: [],
  templateUrl: './excavation-announcement.component.html',
  styleUrls: []
})
export class ExcavationAnnouncementComponent extends ApplicationInfoBaseComponent implements OnInit {

  matchingApplications: Observable<Array<Application>>;

  validityEndTimeCtrl: AbstractControlWarn;
  validityEndTimeIcon: IconConfig = new IconConfig(undefined, true, 'today');
  showReportCustomerDates = false;

  private cableReportIdentifierCtrl: FormControl;
  private winterTimeOperationCtrl: AbstractControl;
  private winterTimeStart: string;
  private winterTimeEnd: string;

  constructor(fb: FormBuilder,
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

  setCableReport(application: Application) {
    this.applicationForm.patchValue({cableReportId: application.id});
  }

  onValidityEndTimePickerClick(picker: MatDatepicker<Date>): void {
    if (this.validityEndTimeCtrl.warnings.inWinterTime) {
      Some(this.validityEndTimeCtrl.value).do(date => {
        this.validityEndTimeCtrl.patchValue(TimeUtil.toSummerTimeStart(date, this.winterTimeEnd));
        this.winterTimeOperationCtrl.patchValue(date);
      });
    } else {
      picker.open();
    }
  }

  reportCustomerDates(status: string): void {
    const data = {
      reporterType: ReporterType.CUSTOMER,
      reportedDates: this.getReportedDatesByStatus(ApplicationStatus[status])
    };

    this.dialog.open(DateReportingModalComponent, {
      ...DATE_REPORTING_MODAL_CONFIG,
      data
    }).afterClosed().pipe(
      filter(result => !!result)
    ).subscribe(result => this.store.dispatch(new ReportCustomerDates(result)));
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, [Validators.required]]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      pksCard: [false],
      constructionWork: [{value: false, disabled: this.readonly}],
      maintenanceWork: [{value: false, disabled: this.readonly}],
      emergencyWork: [{value: false, disabled: this.readonly}],
      propertyConnectivity: [{value: false, disabled: this.readonly}],
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
      cableReportIdentifier: this.cableReportIdentifierCtrl, // to store identifier showed to user
      cableReportId: [undefined],
      workPurpose: ['', Validators.required],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required]
    });

    this.validityEndTimeCtrl = <AbstractControlWarn>this.applicationForm.get(['validityTimes', 'endTime']);
    this.validityEndTimeCtrl.statusChanges.subscribe(status => this.onValidityEndTimeChange(status));

    if (this.applicationStore.isNew) {
      this.validityEndTimeCtrl.markAsDirty(); // To trigger validation
    }

    this.cableReportIdentifierCtrl = this.fb.control(undefined);
    this.winterTimeOperationCtrl = this.applicationForm.controls['winterTimeOperation'];

    this.matchingApplications = this.cableReportIdentifierCtrl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      map(id => ApplicationSearchQuery.forIdAndTypes(id, [ApplicationType[ApplicationType.CABLE_REPORT]])),
      switchMap(search => this.applicationService.search(search)),
      catchError(err => this.notification.errorCatch(err, []))
    );

    combineLatest(
      this.store.select(fromRoot.getConfiguration(ConfigurationKey.WINTER_TIME_START)),
      this.store.select(fromRoot.getConfiguration(ConfigurationKey.WINTER_TIME_END)))
        .pipe(take(1)).subscribe(([start, end]) => {
          this.winterTimeStart = start.value;
          this.winterTimeEnd = end.value;
          this.validityEndTimeCtrl.setValidators(
            [Validators.required, ComplexValidator.inWinterTime(this.winterTimeStart, this.winterTimeEnd)]);
        });

  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const excavation = <ExcavationAnnouncement>application.extension || new ExcavationAnnouncement();
    const form = ExcavationAnnouncementForm.from(application, excavation);
    this.applicationForm.patchValue(form);
    this.patchRelatedCableReport(excavation);
    this.showReportCustomerDates =
      [ApplicationStatus.DECISION, ApplicationStatus.OPERATIONAL_CONDITION].indexOf(application.status) >= 0;
  }

  protected update(form: ExcavationAnnouncementForm): Application {
    const application = super.update(form);
    application.name = 'Kaivuilmoitus'; // Cable reports have no name so set default
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = ExcavationAnnouncementForm.to(form, <ExcavationAnnouncement>application.extension);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  private patchRelatedCableReport(excavation: ExcavationAnnouncement): void {
    if (NumberUtil.isDefined(excavation.cableReportId)) {
      this.applicationService.get(excavation.cableReportId)
        .subscribe(cableReport => this.applicationForm.patchValue({cableReportIdentifier: cableReport.applicationId}));
    }
  }

  private onValidityEndTimeChange(status: any) {
    this.validityEndTimeIcon = this.validityEndTimeCtrl.warnings.inWinterTime
      ? new IconConfig('accent', false, 'warning')
      : new IconConfig(undefined, true, 'today');
  }

  private getReportedDatesByStatus(status: ApplicationStatus): ReportedDateType[] {
    if (status === ApplicationStatus.DECISION) {
      return [ReportedDateType.WINTER_TIME_OPERATION, ReportedDateType.WORK_FINISHED];
    } else if (status === ApplicationStatus.OPERATIONAL_CONDITION) {
      return [ReportedDateType.WORK_FINISHED];
    } else {
      return [];
    }
  }
}
