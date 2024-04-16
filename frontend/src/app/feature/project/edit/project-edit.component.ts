import {Component} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ProjectForm} from './project.form';
import * as fromRoot from '../../allu/reducers';
import * as fromProject from '../reducers';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import * as CustomerSearchAction from '@feature/customerregistry/actions/customer-search-actions';
import * as ContactSearchAction from '@feature/customerregistry/actions/contact-search-actions';
import {select, Store} from '@ngrx/store';
import {Save} from '../actions/project-actions';
import {EnumUtil} from '@util/enum.util';
import {CustomerType} from '@model/customer/customer-type';
import {Customer} from '@model/customer/customer';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatOption} from '@angular/material/core';
import {ComplexValidator} from '@util/complex-validator';
import {Contact} from '@model/customer/contact';
import {Application} from '@model/application/application';
import {ProjectService} from '@service/project/project.service';
import {NumberUtil} from '@util/number.util';
import {debounceTime, filter, map, switchMap, take, takeUntil} from 'rxjs/internal/operators';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {FormUtil} from '@util/form.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {createTranslated} from '@service/error/error-info';
import {ContactSearchQuery} from '@service/customer/contact-search-query';
import {Sort} from '@model/common/sort';

@Component({
  selector: 'project-edit',
  templateUrl: './project-edit.component.html',
  styleUrls: ['./project-edit.component.scss']
})
export class ProjectEditComponent {
  form: FormGroup;
  customerTypes = EnumUtil.enumValues(CustomerType);

  matchingCustomers$: Observable<Customer[]>;
  matchingContacts$: Observable<Contact[]>;
  applications$: Observable<Application[]>;

  private customerTypeCtrl: FormControl;
  private customerCtrl: FormControl;
  private contactCtrl: FormControl;

  private destroy = new Subject<boolean>();

  constructor(private store: Store<fromRoot.State>,
              private fb: FormBuilder,
              private projectService: ProjectService) {
    this.initForm();

    this.initCustomerSearch();
    this.initContactSearch();

    this.store.select(fromProject.getCurrentProject).pipe(
      take(1),
      map(project => ProjectForm.fromProject(project))
    ).subscribe(project => this.form.patchValue(project));

    this.applications$ = this.store.select(fromProject.getIsNewProject).pipe(
      take(1),
      filter(newProject => newProject),
      switchMap(() => this.store.select(fromProject.getPendingApplications))
    );

    this.form.controls['customer'].valueChanges.subscribe(c => this.customerSelected(c));
  }

  selectCustomer(option: MatOption): void {
    this.contactCtrl.reset();
    this.form.get('contactPhone').reset();
    this.form.get('contactEmail').reset();
  }

  selectContact(option: MatOption): void {
    const contact = option.value;
    this.form.patchValue({
      contactPhone: contact.phone,
      contactEmail: contact.email
    });

    if (!this.customerCtrl.value) {
      this.setCustomerForContact(contact);
    }
  }

  onSubmit(form: ProjectForm) {
    if (this.form.valid) {
      const project = ProjectForm.toProject(form);
      this.store.dispatch(new Save(project));
    } else {
      FormUtil.validateFormFields(this.form);
      this.store.dispatch(new NotifyFailure(createTranslated('common.field.faultyValueTitle', 'common.field.faultyValue')));
    }
  }

  customerName(customer?: Customer): string | undefined {
    return customer ? customer.name : undefined;
  }

  contactName(contact?: Contact): string | undefined {
    return contact ? contact.name : undefined;
  }

  private initForm() {
    this.customerTypeCtrl = this.fb.control('', Validators.required);
    this.customerCtrl = this.fb.control(undefined, ComplexValidator.idRequired);
    this.contactCtrl = this.fb.control(undefined, ComplexValidator.idRequired);

    this.form = this.fb.group({
      id: [undefined],
      identifier: ['', Validators.required],
      name: [''],
      customerType: this.customerTypeCtrl,
      customer: this.customerCtrl,
      contact: this.contactCtrl,
      contactPhone: [{value: undefined, disabled: true}],
      contactEmail: [{value: undefined, disabled: true}],
      customerReference: [''],
      additionalInfo: ['']
    });
  }

  private initCustomerSearch(): void {
    this.matchingCustomers$ = this.store.select(fromCustomerSearch.getMatchingCustomerList);

    combineLatest([
      this.customerTypeCtrl.valueChanges,
      this.customerCtrl.valueChanges.pipe(filter(customer => typeof customer === 'string'))
    ]).pipe(
      takeUntil(this.destroy),
      debounceTime(300)
    ).subscribe(([type, name]) => {
      const query = {type, name, invoicingOnly: false, active: true};
      this.store.dispatch(new CustomerSearchAction.Search(ActionTargetType.Customer, {query}));
    });

    this.customerTypeCtrl.valueChanges.pipe(takeUntil(this.destroy))
      .subscribe(() => this.customerCtrl.reset());
  }

  private initContactSearch(): void {
    this.matchingContacts$ = this.store.select(fromCustomerSearch.getMatchingContacts);

    this.contactCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300),
      filter(contact => typeof contact === 'string')
    ).subscribe(name => this.searchContact(name));

    this.customerCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300),
      filter(customer => customer instanceof Customer)
    ).subscribe(customer => this.store.dispatch(new ContactSearchAction.LoadByCustomer(ActionTargetType.Customer, customer.id)));
  }

  private customerSelected(customer: Customer): void {
    if (customer && NumberUtil.isDefined(customer.id)) {
      if (customer.projectIdentifierPrefix) {
        this.projectService.getNextProjectNumber().subscribe(nbr => {
          this.form.patchValue({
            customerType: customer.type,
            identifier: customer.projectIdentifierPrefix + nbr
          }, {emitEvent: false});
        });
      } else {
        this.form.patchValue({customerType: customer.type, identifier: undefined}, {emitEvent: false});
      }
    }
  }

  private searchContact(name: string): void {
    if (this.customerCtrl.value) {
      this.store.dispatch(new ContactSearchAction.SearchForCurrentCustomer(ActionTargetType.Customer, name));
    } else {
      const query: ContactSearchQuery = {name, active: true};
      this.store.dispatch(new ContactSearchAction.Search(ActionTargetType.Customer, {query, sort: new Sort('name', 'asc')}));
    }
  }

  private setCustomerForContact(contact: Contact): void {
    this.store.dispatch(new CustomerSearchAction.FindById(ActionTargetType.Customer, contact.customerId));
    this.store.pipe(
      select(fromCustomerSearch.getCustomersLoading),
      filter(loading => !loading),
      switchMap(() => this.store.pipe(select(fromCustomerSearch.getSelectedCustomer))),
      take(1)
    ).subscribe(customer => this.form.patchValue({
        customerType: customer.type,
        customer
      })
    );
  }
}
