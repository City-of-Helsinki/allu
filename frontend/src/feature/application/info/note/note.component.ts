import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router, NavigationStart} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';

import {Application} from '../../../../model/application/application';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {UrlUtil} from '../../../../util/url.util';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationState} from '../../../../service/application/application-state';
import {NoteForm} from './note.form';
import {ApplicantForm} from '../applicant/applicant.form';


@Component({
  selector: 'note',
  viewProviders: [],
  template: require('./note.component.html'),
  styles: []
})
export class NoteComponent implements OnInit {

  path: string;
  application: Application;
  applicationForm: FormGroup;
  submitPending = false;
  pickadateParams = PICKADATE_PARAMETERS;
  readonly: boolean;

  private routeEvents: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private applicationState: ApplicationState) {
  };

  ngOnInit(): any {
    this.initForm();
    this.application = this.applicationState.application;
    this.applicationForm.patchValue(NoteForm.from(this.application));

    UrlUtil.urlPathContains(this.route.parent, 'summary')
      .filter(contains => contains)
      .forEach(summary => {
        this.readonly = summary;
        this.applicationForm.disable();
      });

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

  onSubmit(form: NoteForm) {
    this.submitPending = true;
    let application = this.update(form);

    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  private update(form: NoteForm) {
    let application = this.application;
    application.name = form.name;
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = NoteForm.to(form);
    return application;
  }

  private initForm() {
    this.applicationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      validityTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['']
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      reoccurring: [false],
      description: ['']
    });
  }
}
