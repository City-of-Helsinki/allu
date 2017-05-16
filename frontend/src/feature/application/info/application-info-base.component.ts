import {OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormGroup} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';
import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';
import {UrlUtil} from '../../../util/url.util';
import {ApplicationForm} from './application-form';
import {ApplicationStatus} from '../../../model/application/application-status';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Some} from '../../../util/option';
import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';

export abstract class ApplicationInfoBaseComponent implements OnInit, OnDestroy {

  application: Application;
  applicationForm: FormGroup;
  readonly: boolean;
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

  onSubmit(form: FormGroup) {
    this.submitPending = true;
    // Enable so that all fields user should not edit are saved also
    // eg. representative, contractor, representative etc.
    form.enable();
    let value = form.value;
    let application = this.update(value);
    application.extension.terms = value.terms;

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
    application.calculatedPriceEuro = form.calculatedPrice;
    application.priceOverrideEuro = form.priceOverride;
    application.priceOverrideReason = form.priceOverrideReason;
    return application;
  };
}
