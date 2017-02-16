import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router, NavigationStart} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../../../model/application/application';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {UrlUtil} from '../../../../util/url.util';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
import {ExcavationAnnouncementForm} from './excavation-announcement.form';
import {ApplicationSearchQuery} from '../../../../model/search/ApplicationSearchQuery';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {Some} from '../../../../util/option';
import {ApplicationState} from '../../../../service/application/application-state';


@Component({
  selector: 'excavation-announcement',
  viewProviders: [],
  template: require('./excavation-announcement.component.html'),
  styles: []
})
export class ExcavationAnnouncementComponent implements OnInit, OnDestroy {

  path: string;
  application: Application;
  applicationForm: FormGroup;
  submitPending = false;
  pickadateParams = PICKADATE_PARAMETERS;
  readonly: boolean;
  cableReportSearch = new Subject<string>();
  matchingApplications: Observable<Array<Application>>;

  private routeEvents: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private applicationHub: ApplicationHub,
              private applicationState: ApplicationState) {
  };

  ngOnInit(): any {
    this.initForm();
    this.application = this.applicationState.application;
    let excavation = <ExcavationAnnouncement>this.application.extension || new ExcavationAnnouncement();
    this.applicationForm.patchValue(ExcavationAnnouncementForm.from(this.application, excavation));

    UrlUtil.urlPathContains(this.route.parent, 'summary')
      .filter(contains => contains)
      .forEach(summary => {
        this.readonly = summary;
        this.applicationForm.disable();
      });

    this.getCableReport(excavation.cableReportId)
      .subscribe(app => {
        this.applicationForm.patchValue({cableReportIdentifier: app.applicationId});
      });

    this.matchingApplications = this.cableReportSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(idSearch => ApplicationSearchQuery.forIdAndTypes(idSearch, [ApplicationType[ApplicationType.CABLE_REPORT]]))
      .switchMap(search => this.applicationHub.searchApplications(search));

    this.routeEvents = this.router.events
      .filter(event => event instanceof NavigationStart)
      .subscribe(navStart => {
        if (!this.readonly) {
          this.applicationState.application = this.update(this.applicationForm.value);
        }
      });
  }

  ngOnDestroy(): any {
    this.routeEvents.unsubscribe();
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
      .orElse(Observable.empty());
  }

  onSubmit(form: ExcavationAnnouncementForm) {
    this.submitPending = true;
    let application = this.update(form);

    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  private update(form: ExcavationAnnouncementForm): Application {
    let application = this.application;
    application.name = 'Kaivuilmoitus'; // Cable reports have no name so set default
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = ExcavationAnnouncementForm.to(form);
    return application;
  }

  private initForm() {
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
      trafficArrangements: ['']
    });
  }
}
