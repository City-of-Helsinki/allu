import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {MatDatepicker} from '@angular/material';
import {Application} from '../../../../model/application/application';
import {AbstractControlWarn, ComplexValidator} from '../../../../util/complex-validator';
import {ExcavationAnnouncementForm} from './excavation-announcement.form';
import {ApplicationSearchQuery} from '../../../../model/search/ApplicationSearchQuery';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {ApplicationStore} from '../../../../service/application/application-store';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {NumberUtil} from '../../../../util/number.util';
import {TimeUtil} from '../../../../util/time.util';
import {Some} from '../../../../util/option';
import {IconConfig} from '../../../common/icon-config';
import {ApplicationService} from '../../../../service/application/application.service';
import {ProjectService} from '../../../../service/project/project.service';
import {catchError, debounceTime, distinctUntilChanged, map, switchMap} from 'rxjs/internal/operators';

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

  private cableReportIdentifierCtrl: FormControl;

  constructor(
    private applicationService: ApplicationService,
    fb: FormBuilder,
    route: ActivatedRoute,
    applicationStore: ApplicationStore,
    notification: NotificationService,
    router: Router,
    projectService: ProjectService) {
    super(fb, route, applicationStore, notification, router, projectService);
  }

  ngOnInit(): any {
    super.ngOnInit();

    this.matchingApplications = this.cableReportIdentifierCtrl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      map(id => ApplicationSearchQuery.forIdAndTypes(id, [ApplicationType[ApplicationType.CABLE_REPORT]])),
      switchMap(search => this.applicationService.search(search)),
      catchError(err => this.notification.errorCatch(err, []))
    );
  }

  setCableReport(application: Application) {
    this.applicationForm.patchValue({cableReportId: application.id});
  }

  onValidityEndTimePickerClick(picker: MatDatepicker<Date>): void {
    if (this.validityEndTimeCtrl.warnings.inWinterTime) {
      Some(this.validityEndTimeCtrl.value)
        .map(date => TimeUtil.toWinterTimeEnd(date))
        .do(date => this.validityEndTimeCtrl.patchValue(date));
    } else {
      picker.open();
    }
  }

  protected initForm() {
    this.cableReportIdentifierCtrl = this.fb.control(undefined);
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, [Validators.required, ComplexValidator.inWinterTime]]
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
      additionalInfo: [''],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required]
    });

    this.validityEndTimeCtrl = <AbstractControlWarn>this.applicationForm.get(['validityTimes', 'endTime']);
    this.validityEndTimeCtrl.statusChanges.subscribe(status => this.onValidityEndTimeChange(status));

    if (this.applicationStore.isNew) {
      this.validityEndTimeCtrl.markAsDirty(); // To trigger validation
    }
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const excavation = <ExcavationAnnouncement>application.extension || new ExcavationAnnouncement();
    const form = ExcavationAnnouncementForm.from(application, excavation);
    this.applicationForm.patchValue(form);
    this.patchRelatedCableReport(excavation);
  }

  protected update(form: ExcavationAnnouncementForm): Application {
    const application = super.update(form);
    application.name = 'Kaivuilmoitus'; // Cable reports have no name so set default
    application.startTime = TimeUtil.toStartDate(form.validityTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.validityTimes.endTime);
    application.extension = ExcavationAnnouncementForm.to(form);

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
}
