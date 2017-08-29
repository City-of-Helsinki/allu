import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {MdDatepicker} from '@angular/material';
import {Application} from '../../../../model/application/application';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {AbstractControlWarn, ComplexValidator} from '../../../../util/complex-validator';
import {ExcavationAnnouncementForm} from './excavation-announcement.form';
import {ApplicationSearchQuery} from '../../../../model/search/ApplicationSearchQuery';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {NumberUtil} from '../../../../util/number.util';
import {TimeUtil, WINTER_TIME_END} from '../../../../util/time.util';
import {Some} from '../../../../util/option';
import {IconConfig} from '../../../common/icon-config';

@Component({
  selector: 'excavation-announcement',
  viewProviders: [],
  template: require('./excavation-announcement.component.html'),
  styles: []
})
export class ExcavationAnnouncementComponent extends ApplicationInfoBaseComponent implements OnInit {

  matchingApplications: Observable<Array<Application>>;

  validityEndTimeCtrl: AbstractControlWarn;
  validityEndTimeIcon: IconConfig = new IconConfig(undefined, true, 'today');

  private cableReportIdentifierCtrl: FormControl;

  constructor(private applicationHub: ApplicationHub,
              fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(fb, route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    let excavation = <ExcavationAnnouncement>this.application.extension || new ExcavationAnnouncement();
    let form = ExcavationAnnouncementForm.from(this.application, excavation);
    this.applicationForm.patchValue(form);
    this.patchRelatedCableReport(excavation);

    this.matchingApplications = this.cableReportIdentifierCtrl.valueChanges
      .debounceTime(300)
      .distinctUntilChanged()
      .map(id => ApplicationSearchQuery.forIdAndTypes(id, [ApplicationType[ApplicationType.CABLE_REPORT]]))
      .switchMap(search => this.applicationHub.searchApplications(search))
      .catch(err => NotificationService.errorCatch(err, []));
  }

  setCableReport(application: Application) {
    this.applicationForm.patchValue({cableReportId: application.id});
  }

  onValidityEndTimePickerClick(picker: MdDatepicker<Date>): void {
    if (this.validityEndTimeCtrl.warnings.inWinterTime) {
      Some(this.validityEndTimeCtrl.value)
        .map(date => TimeUtil.dateWithYear(WINTER_TIME_END.toDate(), date.getFullYear()))
        .do(date => this.validityEndTimeCtrl.patchValue(date));
    } else {
      picker.open();
    }
  }

  protected update(form: ExcavationAnnouncementForm): Application {
    let application = super.update(form);
    application.name = 'Kaivuilmoitus'; // Cable reports have no name so set default
    application.startTime = form.validityTimes.startTime;
    application.endTime = form.validityTimes.endTime;
    application.extension = ExcavationAnnouncementForm.to(form);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected initForm() {
    this.cableReportIdentifierCtrl = this.fb.control(undefined);
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, [Validators.required, ComplexValidator.inWinterTime]]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
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
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      guaranteeEndTime: [undefined],
      customerValidityTimes: this.fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      customerWinterTimeOperation: [undefined],
      customerWorkFinished: [undefined],
      calculatedPrice: [0],
      priceOverride: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      priceOverrideReason: [''],
      cableReportIdentifier: this.cableReportIdentifierCtrl, // to store identifier showed to user
      cableReportId: [undefined],
      additionalInfo: [''],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required]
    });

    this.validityEndTimeCtrl = <AbstractControlWarn>this.applicationForm.get(['validityTimes', 'endTime']);
    this.validityEndTimeCtrl.statusChanges.subscribe(status => this.onValidityEndTimeChange(status));

    if (this.applicationState.isNew) {
      this.validityEndTimeCtrl.markAsDirty(); // To trigger validation
    }
  }

  private patchRelatedCableReport(excavation: ExcavationAnnouncement): void {
    if (NumberUtil.isDefined(excavation.cableReportId)) {
      this.applicationHub.getApplication(excavation.cableReportId)
        .subscribe(cableReport => this.applicationForm.patchValue({cableReportIdentifier: cableReport.applicationId}));
    }
  }

  private onValidityEndTimeChange(status: any) {
    this.validityEndTimeIcon = this.validityEndTimeCtrl.warnings.inWinterTime
      ? new IconConfig('accent', false, 'warning')
      : new IconConfig(undefined, true, 'today');
  }
}
