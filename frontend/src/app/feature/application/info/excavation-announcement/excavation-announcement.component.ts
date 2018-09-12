import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {take} from 'rxjs/internal/operators';
import {MatDatepicker} from '@angular/material';
import {Application} from '../../../../model/application/application';
import {AbstractControlWarn, ComplexValidator} from '../../../../util/complex-validator';
import {ExcavationAnnouncementForm} from './excavation-announcement.form';
import {ApplicationSearchQuery} from '../../../../model/search/ApplicationSearchQuery';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {NumberUtil} from '../../../../util/number.util';
import {TimeUtil} from '../../../../util/time.util';
import {Some} from '../../../../util/option';
import {IconConfig} from '../../../common/icon-config';
import {catchError, debounceTime, distinctUntilChanged, map, switchMap} from 'rxjs/internal/operators';
import * as fromRoot from '@feature/allu/reducers';
import {ConfigurationKey} from '@model/config/configuration-key';

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
  private winterTimeStart: string;
  private winterTimeEnd: string;

  setCableReport(application: Application) {
    this.applicationForm.patchValue({cableReportId: application.id});
  }

  onValidityEndTimePickerClick(picker: MatDatepicker<Date>): void {
    if (this.validityEndTimeCtrl.warnings.inWinterTime) {
      Some(this.validityEndTimeCtrl.value)
        .map(date => TimeUtil.toWinterTimeEnd(date, this.winterTimeStart, this.winterTimeEnd))
        .do(date => this.validityEndTimeCtrl.patchValue(date));
    } else {
      picker.open();
    }
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
}
