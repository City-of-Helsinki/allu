import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {Observable, Subject} from 'rxjs';
import {EnumUtil} from '../../../util/enum.util';
import {NumberUtil} from '../../../util/number.util';
import {ExternalUserHub} from '../../../service/user/external-user-hub';
import {ExternalRoleType} from '../../../model/common/external-role-type';
import {Customer} from '../../../model/customer/customer';
import {ExternalUserForm} from './external-user-form';
import {NotificationService} from '../../notification/notification.service';
import {translations} from '../../../util/translations';
import {CustomerService} from '../../../service/customer/customer.service';
import {debounceTime, filter, map, switchMap} from 'rxjs/internal/operators';
import {ArrayUtil} from '@util/array-util';
import {FormUtil} from '@util/form.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {createTranslated} from '@service/error/error-info';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';

const DEBOUNCE_TIME_MS = 300;

@Component({
  selector: 'external-user',
  templateUrl: './external-user.component.html',
  styleUrls: [
    './external-user.component.scss'
  ]
})
export class ExternalUserComponent implements OnInit {
  userForm: UntypedFormGroup;

  submitted = false;
  roles = EnumUtil.enumValues(ExternalRoleType).sort(ArrayUtil.naturalSortTranslated(['externalUser.role'], (role: string) => role));
  matchingNameCustomers: Observable<Array<Customer>>;
  connectedCustomers: Observable<Array<Customer>>;

  private customerNameControl: UntypedFormControl;
  private connectedCustomersCtrl: UntypedFormControl;
  private connectedCustomers$ = new Subject<Array<Customer>>();

  constructor(private route: ActivatedRoute,
              private userHub: ExternalUserHub,
              private customerService: CustomerService,
              private fb: UntypedFormBuilder,
              private store: Store<fromRoot.State>,
              private notification: NotificationService) {

    this.connectedCustomersCtrl = this.fb.control([]);

    this.userForm = this.fb.group({
      id: undefined,
      username: ['', Validators.required],
      name: ['', Validators.required],
      emailAddress: [undefined, Validators.email],
      active: [true],
      assignedRoles: [[]],
      password: [undefined, Validators.minLength(20)],
      expirationTime: [undefined, Validators.required],
      customerName: [undefined],
      connectedCustomers: this.connectedCustomersCtrl
    });

    this.customerNameControl = <UntypedFormControl>this.userForm.get('customerName');
    this.connectedCustomers = this.connectedCustomers$.asObservable();
  }

  ngOnInit(): void {
    this.route.params.pipe(
      map(params => params.id),
      filter(id => NumberUtil.isDefined(id)),
      switchMap(id => this.userHub.getUser(id)),
      map(user => ExternalUserForm.from(user))
    ).subscribe(user => {
      this.userForm.patchValue(user);
      this.updateConnectedCustomers(user.connectedCustomers);
      this.userForm.get('username').disable(); // username cannot be changed
    });

    this.matchingNameCustomers = this.customerNameControl.valueChanges.pipe(
      debounceTime(DEBOUNCE_TIME_MS),
      switchMap(name => this.customerService.search({name: name}))
    );
  }

  save(): void {
    if (this.userForm.valid) {
      const user = ExternalUserForm.to(this.userForm.getRawValue());
      this.submitted = true;
      this.userHub.saveUser(user).pipe(
        map(savedUser => ExternalUserForm.from(savedUser))
      ).subscribe(savedUser => {
        this.submitted = false;
        this.userForm.patchValue(savedUser);

        const message = translations.externalUser.actions.saved;
        this.notification.success(message);
      });
    } else {
      FormUtil.validateFormFields(this.userForm);
      this.store.dispatch(new NotifyFailure(createTranslated('common.field.faultyValueTitle', 'common.field.faultyValue')));
    }
  }

  addCustomer(customerId: number): void {
    const current = this.connectedCustomersCtrl.value;
    if (!current.some(id => id === customerId)) {
      current.push(customerId);
      this.updateConnectedCustomers(current);
    }
  }

  removeCustomer(customerId: number): void {
    const current = this.connectedCustomersCtrl.value;
    this.updateConnectedCustomers(current.filter(id => id !== customerId));
  }

  private updateConnectedCustomers(customerIds: Array<number>) {
    this.customerService.findByCustomerIds(customerIds)
      .subscribe(customers => {
        this.connectedCustomersCtrl.patchValue(customerIds);
        this.connectedCustomers$.next(customers);
      });
  }
}
