import {OnDestroy, OnInit, AfterViewInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';
import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';
import {UrlUtil} from '../../../util/url.util';
import {ApplicationForm} from './application-form';
import {ApplicationStatus, canBeEdited} from '../../../model/application/application-status';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Some} from '../../../util/option';
import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';
import {CustomerWithContactsForm} from '../../customerregistry/customer/customer-with-contacts.form';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';

export abstract class ApplicationInfoBaseComponent implements OnInit, OnDestroy, AfterViewInit {

  application: Application;
  applicationForm: FormGroup;
  readonly: boolean;
  submitPending = false;
  showTerms = false;

  private appChanges: Subscription;
  private tabChanges: Subscription;
  private hasPropertyDeveloperCtrl: FormControl;
  private hasRepresentativeCtrl: FormControl;

  constructor(protected fb: FormBuilder,
              protected route: ActivatedRoute,
              protected applicationState: ApplicationState) {}

  ngOnInit(): void {
    this.initForm();
    this.hasPropertyDeveloperCtrl = this.fb.control([false]);
    this.hasRepresentativeCtrl = this.fb.control([false]);
    this.applicationForm.addControl('hasPropertyDeveloper', this.hasPropertyDeveloperCtrl);
    this.applicationForm.addControl('hasRepresentative', this.hasRepresentativeCtrl);

    this.appChanges = this.applicationState.applicationChanges.subscribe(app => this.onApplicationChange(app));

    UrlUtil.urlPathContains(this.route.parent, 'summary')
      .filter(contains => contains)
      .forEach(summary => {
        this.readonly = summary || !canBeEdited(this.applicationState.application.statusEnum);
      });

    this.tabChanges = this.applicationState.tabChange.subscribe(tab => {
      if (!this.readonly) {
        this.applicationForm.enable();
        this.applicationState.application = this.update(this.applicationForm.value);
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.readonly) {
      this.applicationForm.disable();
    }
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

  get hasPropertyDeveloper(): boolean {
    return this.hasPropertyDeveloperCtrl.value;
  }

  get hasRepresentative(): boolean {
    return this.hasRepresentativeCtrl.value;
  }

  /**
   * Updates application based on given form and returns updated application
   */
  protected update(form: ApplicationForm): Application {
    let application = this.application;
    application.customersWithContacts = this.getCustomers(form);

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

  private getCustomers(form: ApplicationForm): Array<CustomerWithContacts> {
    let customers = [];
    Some(form.applicant).do(applicant => customers.push(CustomerWithContactsForm.toCustomerWithContacts(applicant)));
    Some(form.contractor).do(contractor => customers.push(CustomerWithContactsForm.toCustomerWithContacts(contractor)));
    Some(form.propertyDeveloper).do(pd => customers.push(CustomerWithContactsForm.toCustomerWithContacts(pd)));
    Some(form.representative).do(representative => customers.push(CustomerWithContactsForm.toCustomerWithContacts(representative)));
    return customers;
  }

  private onApplicationChange(app: Application): void {
    this.application = app;
    this.showTerms = app.status === ApplicationStatus[ApplicationStatus.HANDLING];
    this.applicationForm.patchValue({
      hasPropertyDeveloper: app.propertyDeveloper.customerId,
      hasRepresentative: app.representative.customerId
    });
  }
}
