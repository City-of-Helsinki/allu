import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ProjectForm} from './project.form';
import * as fromProject from '../reducers';
import * as customerSearch from '../actions/customer-search-actions';
import {Store} from '@ngrx/store';
import {Save} from '../actions/project-actions';
import {EnumUtil} from '../../../util/enum.util';
import {CustomerType} from '../../../model/customer/customer-type';
import {Customer} from '../../../model/customer/customer';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {MatOption} from '@angular/material';
import {ComplexValidator} from '../../../util/complex-validator';
import {Contact} from '../../../model/customer/contact';
import {Application} from '../../../model/application/application';
import {ProjectService} from '../../../service/project/project.service';
import {NumberUtil} from '../../../util/number.util';

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

  constructor(private store: Store<fromProject.State>,
              private fb: FormBuilder,
              private projectService: ProjectService) {
    this.initForm();

    this.initCustomerSearch();
    this.initContactSearch();

    this.store.select(fromProject.getCurrentProject).take(1)
      .map(project => ProjectForm.fromProject(project))
      .subscribe(project => this.form.patchValue(project));

    this.applications$ = this.store.select(fromProject.getIsNewProject).take(1)
      .filter(newProject => newProject)
      .switchMap(() => this.store.select(fromProject.getPendingApplications));

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
  }

  onSubmit(form: ProjectForm) {
    const project = ProjectForm.toProject(form);
    this.store.dispatch(new Save(project));
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
    this.matchingCustomers$ = this.store.select(fromProject.getMatchingCustomers);

    Observable.combineLatest(
      this.customerTypeCtrl.valueChanges,
      this.customerCtrl.valueChanges.filter(customer => typeof customer === 'string')
    ).takeUntil(this.destroy)
      .debounceTime(300)
      .subscribe(([type, name]) => this.store.dispatch(new customerSearch.Search(type, name)));

    this.customerTypeCtrl.valueChanges
      .takeUntil(this.destroy)
      .subscribe(() => this.customerCtrl.reset());
  }

  private initContactSearch(): void {
    this.matchingContacts$ = this.store.select(fromProject.getMatchingContacts);

    this.contactCtrl.valueChanges
      .takeUntil(this.destroy)
      .debounceTime(300)
      .filter(contact => typeof contact === 'string')
      .subscribe(name => this.store.dispatch(new customerSearch.SearchContacts(name)));

    this.customerCtrl.valueChanges
      .takeUntil(this.destroy)
      .debounceTime(300)
      .filter(customer => customer instanceof Customer)
      .subscribe(customer => this.store.dispatch(new customerSearch.LoadContacts(customer.id)));
  }

  private customerSelected(customer: Customer): void {
    if (customer && NumberUtil.isDefined(customer.id)) {
      if (customer.projectIdentifierPrefix) {
        this.projectService.getNextProjectNumber().subscribe(nbr => {
          this.form.patchValue({identifier: customer.projectIdentifierPrefix + nbr})
        });
      } else {
        this.form.patchValue({identifier: undefined})
      }
    }
  }
}
