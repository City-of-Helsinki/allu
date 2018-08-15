import {AfterContentInit, Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';
import {UrlUtil} from '../../../util/url.util';
import {ApplicationForm} from './application-form';
import {applicationCanBeEdited, ApplicationStatus} from '../../../model/application/application-status';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Some} from '../../../util/option';
import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';
import {CustomerWithContactsForm} from '../../customerregistry/customer/customer-with-contacts.form';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {EMPTY, Observable, of, Subject} from 'rxjs';
import {SidebarItemType} from '../../sidebar/sidebar-item';
import {FormUtil} from '../../../util/form.util';
import {ProjectService} from '../../../service/project/project.service';
import {distinctUntilChanged, filter, map, switchMap, take, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import {ApplicationService} from '../../../service/application/application.service';
import * as fromRoot from '../reducers';
import * as fromApplication from '../reducers';
import {Store} from '@ngrx/store';
import {
  INFORMATION_ACCEPTANCE_MODAL_CONFIG,
  InformationAcceptanceData,
  InformationAcceptanceModalComponent
} from '../../information-request/acceptance/information-acceptance-modal.component';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {InformationRequestResult} from '../../information-request/information-request-result';
import {SetKindsWithSpecifiers} from '../actions/application-actions';
import {InformationAcceptanceModalEvents} from '../../information-request/acceptance/information-acceptance-modal-events';

/**
 * This component should be used only as base class for other more specific application components.
 * Component would be abstract but Angular does not allow this.
 */
@Component({
  selector: 'application-info-base',
  template: ''
})
export class ApplicationInfoBaseComponent implements OnInit, OnDestroy, AfterContentInit {

  @Output() formDirty: EventEmitter<boolean> = new EventEmitter();

  applicationForm: FormGroup;
  readonly: boolean;
  submitPending = false;
  showTerms = false;
  applicationChanges: Observable<Application>;
  required = FormUtil.required;
  pendingCustomerInfo$: Observable<boolean>;

  protected completeFormStructure: { [key: string]: any; } = {};
  protected draftFormStructure:  { [key: string]: any; } = {};

  protected destroy = new Subject<boolean>();

  private hasPropertyDeveloperCtrl: FormControl;
  private hasRepresentativeCtrl: FormControl;

  constructor(protected fb: FormBuilder,
              protected route: ActivatedRoute,
              protected applicationStore: ApplicationStore,
              protected applicationService: ApplicationService,
              protected notification: NotificationService,
              private router: Router,
              private projectService: ProjectService,
              private store: Store<fromRoot.State>,
              private dialog: MatDialog,
              private modalState: InformationAcceptanceModalEvents) {}

  ngOnInit(): void {
    this.initForm();
    this.applicationForm.valueChanges.subscribe(val => this.formDirty.emit(this.applicationForm.dirty));
    this.hasPropertyDeveloperCtrl = this.fb.control(false);
    this.hasRepresentativeCtrl = this.fb.control(false);
    this.applicationForm.addControl('hasPropertyDeveloper', this.hasPropertyDeveloperCtrl);
    this.applicationForm.addControl('hasRepresentative', this.hasRepresentativeCtrl);

    this.applicationChanges = this.applicationStore.application;

    this.applicationChanges.pipe(takeUntil(this.destroy))
      .subscribe(app => this.onApplicationChange(app));

    this.pendingCustomerInfo$ = this.store.select(fromApplication.hasPendingCustomerInfo);

    this.applicationStore.changes.pipe(
      map(change => change.draft),
      takeUntil(this.destroy),
      distinctUntilChanged()
    ).subscribe(draft => this.onDraftChange(draft));

    this.modalState.isOpen$.pipe(
      takeUntil(this.destroy),
      filter(open => open)
    ).subscribe(() => this.showPendingInfo());
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

  onSubmit(form: FormGroup) {
    this.submitPending = true;

    const value = form.getRawValue();
    const application = this.update(value);

    this.save(application);
  }

  showPendingInfo(): void {
    this.getPendingData()
      .pipe(switchMap(data => this.openAcceptanceModal(data)))
      .subscribe((result: InformationRequestResult) => {
        // TODO: Handle closing information request when implementing it
        this.store.dispatch(new SetKindsWithSpecifiers(result.application.kindsWithSpecifiers));
        this.save(result.application);
      });
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
  protected initForm() {}

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

    this.readonly = UrlUtil.urlPathContains(this.route.parent, 'summary')
      || !applicationCanBeEdited(application.statusEnum);
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
    application.customersWithContacts = this.getCustomers(form);

    Some(form.communication).map(c => {
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
        app => {
          this.applicationSaved(app);
          this.formDirty.emit(false);
        },
        err => {
          this.notification.errorInfo(err);
          this.submitPending = false;
        });
  }

  private applicationSaved(application: Application): void {
    this.notification.success(findTranslation('application.action.saved'));
    this.submitPending = false;

    // We had related project so navigate back to project page
    Some(this.applicationStore.snapshot.relatedProject)
      .do(projectId => this.projectService.addProjectApplication(projectId, application.id)
        .subscribe(() => this.router.navigate(['/projects', projectId])));

    this.router.navigate(['applications', application.id, 'summary']);
  }

  private updateValidators(key: string, formStructure: { [key: string]: any; }, form: FormGroup): void {
    const subStructure = formStructure[key];
    if (subStructure) {
      const subGroup = form.get(key);

      // Handle nester form groups recursively
      if (subGroup instanceof FormGroup) {
        Object.keys(subStructure).forEach(subKey => this.updateValidators(subKey, subStructure, subGroup));
      } else if (subGroup) {
        const validators = subStructure.length > 1 ? subStructure[1] : [];
        subGroup.setValidators(validators);
        subGroup.updateValueAndValidity();
      }
    }
  }

  private getPendingData(): Observable<InformationAcceptanceData> {
    return this.store.select(fromApplication.pendingClientDataFields).pipe(
      withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
      switchMap(([pending, current]) => {
        if (pending.length) {
          return of({
            oldInfo: current,
            newInfo: current,
            updatedFields: pending
          });
        } else {
          return EMPTY;
        }
      }),
      take(1));
  }

  private openAcceptanceModal(data: InformationAcceptanceData): Observable<InformationRequestResult>  {
    data.readonly = this.applicationStore.snapshot.application.status === ApplicationStatus[ApplicationStatus.PENDING_CLIENT];
    const config: MatDialogConfig<InformationAcceptanceData> = {...INFORMATION_ACCEPTANCE_MODAL_CONFIG, data};
    return this.dialog
      .open<InformationAcceptanceModalComponent>(InformationAcceptanceModalComponent, config)
      .afterClosed()
      .pipe(filter(result => !!result));
  }
}
