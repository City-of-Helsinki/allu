import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../../../model/application/application';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {LocationState} from '../../../../service/application/location-state';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {UrlUtil} from '../../../../util/url.util';
import {MaterializeUtil} from '../../../../util/materialize.util';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
import {ExcavationAnnouncementForm} from './excavation-announcement.form';
import {ApplicationSearchQuery} from '../../../../model/search/ApplicationSearchQuery';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {Some} from '../../../../util/option';


@Component({
  selector: 'excavation-announcement',
  viewProviders: [],
  template: require('./excavation-announcement.component.html'),
  styles: []
})
export class ExcavationAnnouncementComponent implements OnInit {

  path: string;
  application: Application;
  applicationForm: FormGroup;
  submitPending = false;
  pickadateParams = PICKADATE_PARAMETERS;
  readonly: boolean;
  cableReportSearch = new Subject<string>();
  matchingApplications: Observable<Array<Application>>;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private fb: FormBuilder,
              private locationState: LocationState,
              private applicationHub: ApplicationHub) {
  };

  ngOnInit(): any {
    this.initForm();

    this.route.parent.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;
        this.application.type = this.route.routeConfig.path;

        UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
          this.readonly = summary;
        });

        let excavation = <ExcavationAnnouncement>application.extension || new ExcavationAnnouncement();
        this.applicationForm.patchValue(ExcavationAnnouncementForm.from(application, excavation));

        this.getCableReport(excavation.cableReportId)
          .subscribe(app => {
            this.applicationForm.patchValue({cableReportIdentifier: app.applicationId});
            MaterializeUtil.updateTextFields(10);
          });

        if (this.readonly) {
          this.applicationForm.disable();
        }
      });

    this.matchingApplications = this.cableReportSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(idSearch => ApplicationSearchQuery.forIdAndTypes(idSearch, [ApplicationType[ApplicationType.CABLE_REPORT]]))
      .switchMap(search => this.applicationHub.searchApplications(search));
  }

  ngOnDestroy(): any {
  }

  onIdentifierSearchChange(identifier: string) {
    this.cableReportSearch.next(identifier);
  }

  setCableReportId(id: number) {
    this.applicationForm.patchValue({cableReportId: id});
  }

  getCableReport(applicationId: number): Observable<Application> {
    return Some(applicationId)
      .map(id => this.applicationHub.getApplication(id))
      .orElse(Observable.empty());
  }

  onSubmit(form: ExcavationAnnouncementForm) {
    this.submitPending = true;
    let application = this.application;
    application.name = 'Kaivuilmoitus'; // Cable reports have no name so set default
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = ExcavationAnnouncementForm.to(form);

    this.applicationHub.save(application).subscribe(app => {
      this.locationState.clear();
      this.submitPending = false;
      this.router.navigate(['applications', app.id, 'summary']);
    }, err => {
      this.submitPending = false;
    });
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
      plotConnectivity: [{value: false, disabled: this.readonly}],
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
