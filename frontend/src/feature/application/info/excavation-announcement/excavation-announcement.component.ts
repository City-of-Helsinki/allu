import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../../../model/application/application';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ComplexValidator} from '../../../../util/complex-validator';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {ExcavationAnnouncementForm} from './excavation-announcement.form';
import {ApplicationSearchQuery} from '../../../../model/search/ApplicationSearchQuery';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {Some} from '../../../../util/option';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {NumberUtil} from '../../../../util/number.util';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';


@Component({
  selector: 'excavation-announcement',
  viewProviders: [],
  template: require('./excavation-announcement.component.html'),
  styles: []
})
export class ExcavationAnnouncementComponent extends ApplicationInfoBaseComponent implements OnInit {

  matchingApplications: Observable<Array<Application>>;

  private cableReportIdentifierCtrl: FormControl;

  constructor(private applicationHub: ApplicationHub,
              private fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
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

  getCableReport(applicationId: number): Observable<Application> {
    return Some(applicationId)
      .map(id => this.applicationHub.getApplication(id))
      .orElse(Observable.empty())
      .catch(err => NotificationService.errorCatch(err));
  }

  protected update(form: ExcavationAnnouncementForm): Application {
    let application = super.update(form);
    application.name = 'Kaivuilmoitus'; // Cable reports have no name so set default
    application.startTime = form.validityTimes.startTime;
    application.endTime = form.validityTimes.endTime;
    application.extension = ExcavationAnnouncementForm.to(form, application.extension.specifiers);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected initForm() {
    this.cableReportIdentifierCtrl = this.fb.control(undefined);
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      pksCard: [false],
      constructionWork: [{value: false, disabled: this.readonly}],
      maintenanceWork: [{value: false, disabled: this.readonly}],
      emergencyWork: [{value: false, disabled: this.readonly}],
      propertyConnectivity: [{value: false, disabled: this.readonly}],
      winterTimeOperation: [undefined],
      summerTimeOperation: [undefined],
      workFinished: [undefined],
      unauthorizedWork: this.fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      guaranteeEndTime: [undefined],
      calculatedPrice: [0],
      priceOverride: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      priceOverrideReason: [''],
      cableReportIdentifier: this.cableReportIdentifierCtrl, // to store identifier showed to user
      cableReportId: [undefined],
      additionalInfo: [''],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required]
    });
  }

  private patchRelatedCableReport(excavation: ExcavationAnnouncement): void {
    if (NumberUtil.isDefined(excavation.cableReportId)) {
      this.applicationHub.getApplication(excavation.cableReportId)
        .subscribe(cableReport => this.applicationForm.patchValue({cableReportIdentifier: cableReport.applicationId}));
    }
  }
}
