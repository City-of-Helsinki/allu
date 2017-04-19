import {OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormGroup} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';
import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';
import {UrlUtil} from '../../../util/url.util';
import {PICKADATE_PARAMETERS} from '../../../util/time.util';
import {ApplicationForm} from './application-form';
import {ApplicationStatus} from '../../../model/application/application-status';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Some} from '../../../util/option';
import {DistributionType} from '../../../model/common/distribution-type';
import {DistributionEntryForm} from '../distribution-list/distribution-entry-form';

export abstract class ApplicationInfoBaseComponent implements OnInit, OnDestroy {

  application: Application;
  applicationForm: FormGroup;
  readonly: boolean;
  pickadateParams = PICKADATE_PARAMETERS;
  submitPending = false;
  showTerms = false;

  private appChanges: Subscription;
  private tabChanges: Subscription;

  constructor(protected route: ActivatedRoute,
              protected applicationState: ApplicationState) {}

  ngOnInit(): void {
    this.initForm();
    this.appChanges = this.applicationState.applicationChanges.subscribe(app => {
      this.application = app;
      this.showTerms = app.status === ApplicationStatus[ApplicationStatus.HANDLING];
    });

    UrlUtil.urlPathContains(this.route.parent, 'summary')
      .filter(contains => contains)
      .forEach(summary => {
        this.readonly = summary;
        this.applicationForm.disable();
      });

    this.tabChanges = this.applicationState.tabChange.subscribe(tab => {
      if (!this.readonly) {
        this.applicationForm.enable();
        this.applicationState.application = this.update(this.applicationForm.value);
      }
    });
  }

  ngOnDestroy(): any {
    this.appChanges.unsubscribe();
    this.tabChanges.unsubscribe();
  }

  /**
   * Initializes application form
   */
  protected abstract initForm();

  onSubmit(form: ApplicationForm) {
    this.submitPending = true;
    let application = this.update(form);
    application.extension.terms = form.terms;

    this.applicationState.save(application)
      .subscribe(
        app => {
          NotificationService.message(findTranslation('application.action.saved'));
          this.submitPending = false;
        },
        err => {
          NotificationService.error(err);
          this.submitPending = false;
        });
  }

  /**
   * Updates application based on given form and returns updated application
   */
  protected update(form: ApplicationForm): Application {
    let application = this.application;

    Some(form.communication).map(c => {
      application.decisionDistributionType = c.distributionType;
      application.decisionPublicityType = c.publicityType;
      application.decisionDistributionList = c.distributionRows.map(distribution => DistributionEntryForm.to(distribution));
    });
    return application;
  };
}
