import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../../../model/application/application';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
import {ExcavationAnnouncementForm} from './excavation-announcement.form';
import {ApplicationSearchQuery} from '../../../../model/search/ApplicationSearchQuery';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {Some} from '../../../../util/option';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {NotificationService} from '../../../../service/notification/notification.service';


@Component({
  selector: 'excavation-announcement',
  viewProviders: [],
  template: require('./excavation-announcement.component.html'),
  styles: []
})
export class ExcavationAnnouncementComponent extends ApplicationInfoBaseComponent implements OnInit {

  cableReportSearch = new Subject<string>();
  matchingApplications: Observable<Array<Application>>;

  constructor(private applicationHub: ApplicationHub,
              private fb: FormBuilder,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    let excavation = <ExcavationAnnouncement>this.application.extension || new ExcavationAnnouncement();
    this.applicationForm.patchValue(ExcavationAnnouncementForm.from(this.application, excavation));

    this.matchingApplications = this.cableReportSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(idSearch => ApplicationSearchQuery.forIdAndTypes(idSearch, [ApplicationType[ApplicationType.CABLE_REPORT]]))
      .switchMap(search => this.applicationHub.searchApplications(search))
      .catch(err => NotificationService.errorCatch(err, []));
  }

  onIdentifierSearchChange(identifier: string) {
    this.cableReportSearch.next(identifier);
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
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = ExcavationAnnouncementForm.to(form, application.extension.specifiers);

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    return application;
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['', Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      pksCard: [false],
      constructionWork: [{value: false, disabled: this.readonly}],
      maintenanceWork: [{value: false, disabled: this.readonly}],
      emergencyWork: [{value: false, disabled: this.readonly}],
      propertyConnectivity: [{value: false, disabled: this.readonly}],
      winterTimeOperation: [''],
      summerTimeOperation: [''],
      workFinished: [''],
      unauthorizedWork: this.fb.group({
        startTime: [''],
        endTime: ['']
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      guaranteeEndTime: [''],
      cableReportIdentifier: [''], // to store identifier showed to user
      cableReportId: [undefined],
      additionalInfo: [''],
      trafficArrangements: [''],
      trafficArrangementImpedimentType: ['', Validators.required]
    });
  }
}
