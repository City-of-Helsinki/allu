import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Subject} from 'rxjs/Subject';
import {EnumUtil} from '../../../util/enum.util';
import {NumberUtil} from '../../../util/number.util';
import {ExternalUserHub} from '../../../service/user/external-user-hub';
import {ExternalRoleType} from '../../../model/common/external-role-type';
import {Customer} from '../../../model/customer/customer';
import {Observable} from 'rxjs/Observable';
import {emailValidator} from '../../../util/complex-validator';
import {ExternalUserForm} from './external-user-form';
import {NotificationService} from '../../../service/notification/notification.service';
import {translations} from '../../../util/translations';
import {CustomerService} from '../../../service/customer/customer.service';

const DEBOUNCE_TIME_MS = 300;

@Component({
  selector: 'external-user',
  templateUrl: './external-user.component.html',
  styleUrls: [
    './external-user.component.scss'
  ]
})
export class ExternalUserComponent implements OnInit {
  userForm: FormGroup;

  submitted = false;
  roles = EnumUtil.enumValues(ExternalRoleType);
  matchingNameCustomers: Observable<Array<Customer>>;
  connectedCustomers: Observable<Array<Customer>>;

  private customerNameControl: FormControl;
  private connectedCustomersCtrl: FormControl;
  private connectedCustomers$ = new Subject<Array<Customer>>();

  constructor(private route: ActivatedRoute,
              private userHub: ExternalUserHub,
              private customerService: CustomerService,
              private fb: FormBuilder) {

    this.connectedCustomersCtrl = this.fb.control([]);

    this.userForm = this.fb.group({
      id: undefined,
      username: ['', Validators.required],
      name: ['', Validators.required],
      emailAddress: [undefined, emailValidator],
      active: [true],
      assignedRoles: [[]],
      token: [{value: undefined, disabled: true}],
      expirationTime: [undefined, Validators.required],
      customerName: [undefined],
      connectedCustomers: this.connectedCustomersCtrl
    });

    this.customerNameControl = <FormControl>this.userForm.get('customerName');
    this.connectedCustomers = this.connectedCustomers$.asObservable();
  }

  ngOnInit(): void {
    this.route.params
      .map(params => params.id)
      .filter(id => NumberUtil.isDefined(id))
      .switchMap(id => this.userHub.getUser(id))
      .map(user => ExternalUserForm.from(user))
      .subscribe(user => {
        this.userForm.patchValue(user);
        this.updateConnectedCustomers(user.connectedCustomers);
        this.userForm.get('username').disable(); // username cannot be changed
      });

    this.matchingNameCustomers = this.customerNameControl.valueChanges
      .debounceTime(DEBOUNCE_TIME_MS)
      .switchMap(name => this.customerService.search({name: name}));
  }

  save(): void {
    const user = ExternalUserForm.to(this.userForm.getRawValue());
    this.submitted = true;
    const currentCustomerToken = this.userForm.getRawValue().token;
    this.userHub.saveUser(user)
      .map(savedUser => ExternalUserForm.from(savedUser))
      .subscribe(savedUser => {
        this.submitted = false;
        this.userForm.patchValue(savedUser);

        let message = translations.externalUser.actions.saved;
        if (currentCustomerToken !== savedUser.token) {
          message += '<br>' + translations.externalUser.actions.customerTokenGenerated;
        }
        NotificationService.message(message);
    });
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
