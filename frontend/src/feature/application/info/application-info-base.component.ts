import {OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormGroup} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';

import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';
import {UrlUtil} from '../../../util/url.util';
import {PICKADATE_PARAMETERS} from '../../../util/time.util';
import {ApplicationForm} from './application-form';

export abstract class ApplicationInfoBaseComponent implements OnInit, OnDestroy {

  application: Application;
  applicationForm: FormGroup;
  readonly: boolean;
  pickadateParams = PICKADATE_PARAMETERS;
  submitPending = false;

  private tabChanges: Subscription;

  constructor(protected route: ActivatedRoute,
              protected applicationState: ApplicationState) {}

  ngOnInit(): void {
    this.initForm();
    this.application = this.applicationState.application;

    UrlUtil.urlPathContains(this.route.parent, 'summary')
      .filter(contains => contains)
      .forEach(summary => {
        this.readonly = summary;
        this.applicationForm.disable();
      });

    this.tabChanges = this.applicationState.tabChange.subscribe(tab => {
      if (!this.readonly) {
        this.applicationState.application = this.update(this.applicationForm.value);
      }
    });
  }

  ngOnDestroy(): any {
    this.tabChanges.unsubscribe();
  }

  /**
   * Initializes application form
   */
  protected abstract initForm();

  onSubmit(form: ApplicationForm) {
    this.submitPending = true;
    let application = this.update(form);

    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  /**
   * Updates application based on given form and returns updated application
   */
  protected abstract update(form: ApplicationForm): Application;
}
