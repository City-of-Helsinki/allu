import {AfterContentInit, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';
import {UrlUtil} from '../../../util/url.util';
import {ApplicationForm} from './application-form';
import {ApplicationStatus, canBeEdited} from '../../../model/application/application-status';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Some} from '../../../util/option';
import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';
import {CustomerWithContactsForm} from '../../customerregistry/customer/customer-with-contacts.form';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import {SidebarItemType} from '../../sidebar/sidebar-item';
import {ProjectHub} from '../../../service/project/project-hub';

export abstract class ApplicationInfoBaseComponent implements OnInit, OnDestroy, AfterContentInit {

  applicationForm: FormGroup;
  readonly: boolean;
  submitPending = false;
  showTerms = false;
  applicationChanges: Observable<Application>;

  protected destroy = new Subject<boolean>();

  private hasPropertyDeveloperCtrl: FormControl;
  private hasRepresentativeCtrl: FormControl;

  constructor(protected fb: FormBuilder,
              protected route: ActivatedRoute,
              protected applicationStore: ApplicationStore,
              private router: Router,
              private projectHub: ProjectHub) {}

  ngOnInit(): void {
    this.initForm();
    this.hasPropertyDeveloperCtrl = this.fb.control(false);
    this.hasRepresentativeCtrl = this.fb.control(false);
    this.applicationForm.addControl('hasPropertyDeveloper', this.hasPropertyDeveloperCtrl);
    this.applicationForm.addControl('hasRepresentative', this.hasRepresentativeCtrl);

    UrlUtil.urlPathContains(this.route.parent, 'summary')
      .filter(contains => contains)
      .forEach(summary => {
        this.readonly = summary || !canBeEdited(this.applicationStore.snapshot.application.statusEnum);
      });

    this.applicationChanges = this.applicationStore.application;

    this.applicationChanges
      .takeUntil(this.destroy)
      .subscribe(app => this.onApplicationChange(app));
  }

  ngAfterContentInit(): void {
    if (this.readonly) {
      this.applicationForm.disable();
    }

    this.applicationStore.tab
      .takeUntil(this.destroy)
      .subscribe(tab => this.onTabChange(tab));
  }

  ngOnDestroy(): any {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onSubmit(form: FormGroup) {
    this.submitPending = true;

    const value = form.getRawValue();
    const application = this.update(value);

    this.save(application);
  }

  get hasPropertyDeveloper(): boolean {
    return this.hasPropertyDeveloperCtrl.value;
  }

  get hasRepresentative(): boolean {
    return this.hasRepresentativeCtrl.value;
  }

  /**
   * Initializes application form
   */
  protected abstract initForm();

  /**
   * Handles application changes
   */
  protected onApplicationChange(application: Application): void {
    this.showTerms = application.statusEnum >= ApplicationStatus.HANDLING;
    this.applicationForm.patchValue({
      hasPropertyDeveloper: application.propertyDeveloper.customerId,
      hasRepresentative: application.representative.customerId,
      invoiceRecipientId: application.invoiceRecipientId
    });
  }

  /**
   * Updates application based on given form and returns updated application
   */
  protected update(form: ApplicationForm): Application {
    const application = this.applicationStore.snapshot.application;
    application.customersWithContacts = this.getCustomers(form);

    Some(form.communication).map(c => {
      application.decisionDistributionType = c.distributionType;
      application.decisionPublicityType = c.publicityType;
      application.decisionDistributionList = c.distributionRows.map(distribution => DistributionEntryForm.to(distribution));
    });
    return application;
  }

  private getCustomers(form: ApplicationForm): Array<CustomerWithContacts> {
    const customers = [];
    Some(form.applicant).do(applicant => customers.push(CustomerWithContactsForm.toCustomerWithContacts(applicant)));
    Some(form.contractor).do(contractor => customers.push(CustomerWithContactsForm.toCustomerWithContacts(contractor)));
    Some(form.propertyDeveloper).do(pd => customers.push(CustomerWithContactsForm.toCustomerWithContacts(pd)));
    Some(form.representative).do(representative => customers.push(CustomerWithContactsForm.toCustomerWithContacts(representative)));
    return customers;
  }

  private onTabChange(tab: SidebarItemType): void {
    if (!this.readonly) {
      this.applicationForm.enable();
      this.applicationStore.applicationChange(this.update(this.applicationForm.getRawValue()));
    }
  }

  private save(application: Application) {
    this.applicationStore.save(application)
      .subscribe(
        app => this.applicationSaved(app),
        err => {
          NotificationService.error(err);
          this.submitPending = false;
        });
  }

  private applicationSaved(application: Application): void {
    NotificationService.message(findTranslation('application.action.saved'));
    this.submitPending = false;

    // We had related project so navigate back to project page
    Some(this.applicationStore.snapshot.relatedProject)
      .do(projectId => this.projectHub.addProjectApplication(projectId, application.id)
        .subscribe(project => this.router.navigate(['/projects', project.id])));

    this.router.navigate(['applications', application.id, 'summary']);
  }
}
