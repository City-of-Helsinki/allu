import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router, NavigationStart} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';

import {Application} from '../../../../model/application/application';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {UrlUtil} from '../../../../util/url.util';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicantForm} from '../applicant/applicant.form';
import {ApplicationState} from '../../../../service/application/application-state';
import {PlacementContract} from '../../../../model/application/placement-contract/placement-contract';
import {PlacementContractForm} from './placement-contract.form';


@Component({
  selector: 'placement-contract',
  viewProviders: [],
  template: require('./placement-contract.component.html'),
  styles: []
})
export class PlacementContractComponent implements OnInit, OnDestroy {

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
    let contract = <PlacementContract>this.application.extension || new PlacementContract();
    this.applicationForm.patchValue(PlacementContractForm.from(this.application, contract));

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

  onSubmit(form: PlacementContractForm) {
    this.submitPending = true;
    let application = this.update(form);

    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  private update(form: PlacementContractForm): Application {
    let application = this.application;
    application.name = 'Sijoitussopimus'; // Placement contracts have no name so set default
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = PlacementContractForm.to(form);
    return application;
  }

  private initForm() {
    this.applicationForm = this.fb.group({
      validityTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['', Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      diaryNumber: [''],
      additionalInfo: [''],
      generalTerms: ['']
    });
  }
}
