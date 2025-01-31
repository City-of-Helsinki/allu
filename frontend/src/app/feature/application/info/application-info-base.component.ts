import {AfterContentInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';
import {UrlUtil} from '../../../util/url.util';
import {ApplicationForm} from './application-form';
import {applicationCanBeEdited, ApplicationStatus, isSameOrAfter} from '../../../model/application/application-status';
import {NotificationService} from '../../notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Some} from '../../../util/option';
import {CustomerWithContactsForm} from '../../customerregistry/customer/customer-with-contacts.form';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {Observable, Subject} from 'rxjs';
import {SidebarItemType} from '../../sidebar/sidebar-item';
import {FormUtil} from '../../../util/form.util';
import {ProjectService} from '../../../service/project/project.service';
import {distinctUntilChanged, map, switchMap, take, takeUntil, tap} from 'rxjs/operators';
import {ApplicationService} from '../../../service/application/application.service';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '../reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {select, Store} from '@ngrx/store';
import {InformationRequest} from '@model/information-request/information-request';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {createTranslated} from '@service/error/error-info';
import {DistributionEntry} from '@model/common/distribution-entry';
import {SaveDistribution} from '@feature/application/actions/application-actions';
import {DistributionComponent} from '@feature/application/distribution/distribution.component';

/**
 * This component should be used only as base class for other more specific application components.
 * Component would be abstract but Angular does not allow this.
 */
@Component({
  selector: 'application-info-base',
  template: ''
})
export class ApplicationInfoBaseComponent implements OnInit, OnDestroy, AfterContentInit {
  @Input() applicationForm: UntypedFormGroup;

  @ViewChild(DistributionComponent) distributionComponent: DistributionComponent;

  readonly: boolean = true;
  submitPending = false;
  showTerms = false;
  applicationChanges: Observable<Application>;
  required = FormUtil.required;
  informationRequest$: Observable<InformationRequest>;
  pendingClientData$: Observable<boolean>;
  pendingCustomerInfo$: Observable<boolean>;
  pendingInformationRequestResponse$: Observable<boolean>;
  distribution$: Observable<DistributionEntry[]>;

  protected completeFormStructure: { [key: string]: any; } = {};
  protected draftFormStructure:  { [key: string]: any; } = {};

  protected destroy = new Subject<boolean>();

  private hasPropertyDeveloperCtrl: UntypedFormControl;
  private hasRepresentativeCtrl: UntypedFormControl;

  constructor(protected fb: UntypedFormBuilder,
              protected route: ActivatedRoute,
              protected applicationStore: ApplicationStore,
              protected applicationService: ApplicationService,
              protected notification: NotificationService,
              private router: Router,
              private projectService: ProjectService,
              protected store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.applicationStore.changeTab('BASIC_INFO');
    this.initForm();
    this.hasPropertyDeveloperCtrl = this.fb.control(false);
    this.hasRepresentativeCtrl = this.fb.control(false);
    this.applicationForm.addControl('hasPropertyDeveloper', this.hasPropertyDeveloperCtrl);
    this.applicationForm.addControl('hasRepresentative', this.hasRepresentativeCtrl);

    this.applicationChanges = this.store.pipe(select(fromApplication.getCurrentApplication));

    this.applicationChanges.pipe(takeUntil(this.destroy))
      .subscribe(app => this.onApplicationChange(app));

    this.pendingClientData$ = this.store.select(fromApplication.hasPendingClientData);
    this.pendingCustomerInfo$ = this.store.select(fromApplication.hasPendingCustomerInfo);
    this.pendingInformationRequestResponse$ = this.store.pipe(select(fromInformationRequest.getActiveInformationRequestResponsePending));

    this.informationRequest$ = this.store.pipe(select(fromInformationRequest.getActiveInformationRequest));

    this.applicationStore.changes.pipe(
      map(change => change.draft),
      takeUntil(this.destroy),
      distinctUntilChanged()
    ).subscribe(draft => this.onDraftChange(draft));

    this.distribution$ = this.store.pipe(select(fromApplication.getDistributionList));

  }

  ngAfterContentInit(): void {
    if (this.readonly) {
      this.applicationForm.disable();
    }

    this.applicationStore.tab.pipe(takeUntil(this.destroy))
      .subscribe(tab => this.onTabChange(tab));
  }

  ngOnDestroy(): any {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onSubmit(form: UntypedFormGroup) {
    if (form.valid) {
      this.submitPending = true;
      this.savePendingDistribution();
      const value = form.getRawValue();
      const application = this.update(value);
      this.save(application);
    } else {
      FormUtil.validateFormFields(form);
      this.onValidationErrors();
    }
  }

  saveDistribution(distribution: DistributionEntry[]): void {
    this.store.dispatch(new SaveDistribution(distribution));
  }

  get hasPropertyDeveloper(): boolean {
    return this.hasPropertyDeveloperCtrl.value;
  }

  get hasRepresentative(): boolean {
    return this.hasRepresentativeCtrl.value;
  }

  /**
   * Initializes application form with type specific data
   */
  protected initForm() {
    const extensionForm = this.createExtensionForm();
    FormUtil.addControls(this.applicationForm, extensionForm.controls);
  }

  /**
   * Create application type specific form
   */
  protected createExtensionForm(): UntypedFormGroup {
    return this.fb.group({});
  }

  /**
   * Handles application changes
   */
  protected onApplicationChange(application: Application): void {
    this.showTerms = isSameOrAfter(application.status, ApplicationStatus.HANDLING);
    this.applicationForm.patchValue({
      name: application.name,
      hasPropertyDeveloper: application.propertyDeveloper.customerId,
      hasRepresentative: application.representative.customerId,
      invoiceRecipientId: application.invoiceRecipientId
    }, {emitEvent: false});

    setTimeout(() => {
      this.readonly = UrlUtil.urlPathContains(this.route.parent, 'summary') || !applicationCanBeEdited(application);
    }, 0);
  }

  /**
   * Handles application draft status changes
   */
  protected onDraftChange(draft: boolean): void {
    if (draft) {
      Object.keys(this.draftFormStructure)
        .forEach(key => this.updateValidators(key, this.draftFormStructure, this.applicationForm));
    } else {
      Object.keys(this.completeFormStructure)
        .forEach(key => this.updateValidators(key, this.completeFormStructure, this.applicationForm));
    }
  }

  /**
   * Updates application based on given form and returns updated application
   */
  protected update(form: ApplicationForm): Application {
    const application = this.applicationStore.snapshot.application;
    application.name = form.name;
    application.customersWithContacts = this.getCustomers(form);
    application.receivedTime = form.receivedTime;

    Some(form.communication).map(c => {
      application.decisionPublicityType = c.publicityType;
    });
    return application;
  }

  protected onValidationErrors(): void {
    this.store.dispatch(new NotifyFailure(createTranslated('common.field.faultyValueTitle', 'common.field.faultyValue')));
  }

  private getCustomers(form: ApplicationForm): Array<CustomerWithContacts> {
    const customers = [];
    Some(form.applicant).do(applicant => customers.push(CustomerWithContactsForm.toCustomerWithContacts(applicant)));
    Some(form.contractor).do(contractor => customers.push(CustomerWithContactsForm.toCustomerWithContacts(contractor)));
    Some(form.propertyDeveloper).do(pd => customers.push(CustomerWithContactsForm.toCustomerWithContacts(pd)));
    Some(form.representative).do(representative => customers.push(CustomerWithContactsForm.toCustomerWithContacts(representative)));
    return customers;
  }

  /**
   * Handle tab change events
   *
   * No need to do anything if
   * 1. readonly mode
   * 2. we already are in the basic info tab (eg. no actual tab change)
   * */
  private onTabChange(tab: SidebarItemType): void {
    if (!this.readonly && tab !== 'BASIC_INFO') {
      this.applicationStore.applicationChange(this.update(this.applicationForm.getRawValue()));
    }
  }

  private save(application: Application) {
    this.store.pipe(
      select(fromApplication.getDistributionList),
      take(1),
      switchMap(distribution => this.applicationStore.save(application).pipe(
        tap(app => this.store.dispatch(new SaveDistribution(distribution)))
      ))
    ).subscribe(
      app => this.applicationSaved(app),
      err => {
        this.notification.errorInfo(err);
        this.submitPending = false;
      }
    );
  }

  private applicationSaved(application: Application): void {
    this.applicationForm.markAsPristine();
    this.notification.success(findTranslation('application.action.saved'));
    this.submitPending = false;

    // We had related project so navigate back to project page
    Some(this.applicationStore.snapshot.relatedProject)
      .do(projectId => this.projectService.addProjectApplication(projectId, application.id)
        .subscribe(() => this.router.navigate(['/projects', projectId])));

    this.router.navigate(['applications', application.id, 'summary']);
  }

  private updateValidators(key: string, formStructure: { [key: string]: any; }, form: UntypedFormGroup): void {
    const subStructure = formStructure[key];
    if (subStructure) {
      const subGroup = form.get(key);

      // Handle nester form groups recursively
      if (subGroup instanceof UntypedFormGroup) {
        Object.keys(subStructure).forEach(subKey => this.updateValidators(subKey, subStructure, subGroup));
      } else if (subGroup) {
        const validators = subStructure.length > 1 ? subStructure[1] : [];
        subGroup.setValidators(validators);
        subGroup.updateValueAndValidity();
      }
    }
  }

  private savePendingDistribution(): void {
    // Application type might not have distribution
    if (this.distributionComponent) {
      this.distributionComponent.savePending();
    }
  }
}
