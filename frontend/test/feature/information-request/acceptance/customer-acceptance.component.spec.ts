import {Component, DebugElement, Input, NgModule} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Customer} from '@model/customer/customer';
import {CodeSet, CodeSetCodeMap, CodeSetTypeMap} from '@model/codeset/codeset';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import * as fromCodeSet from '@feature/allu/reducers/code-set-reducer';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {SearchSuccess} from '@feature/customerregistry/actions/customer-search-actions';
import * as CodeSetAction from '@feature/allu/actions/code-set-actions';
import {By} from '@angular/platform-browser';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {CustomerModalComponent} from '@feature/information-request/acceptance/customer/customer-modal.component';
import {CustomerService} from '@service/customer/customer.service';
import {CustomerServiceMock, MatDialogMock, MatDialogRefMock, NotificationServiceMock} from '../../../mocks';
import {NotificationService} from '@feature/notification/notification.service';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {of} from 'rxjs';
import {CustomerAcceptanceComponent} from '@feature/information-request/acceptance/customer/customer-acceptance.component';
import {CustomerType} from '@model/customer/customer-type';
import {Page} from '@model/common/page';
import {CustomerOptionContentComponent} from '@feature/customerregistry/customer/customer-option-content.component';

@Component({
  selector: 'host',
  template: `
    <form [formGroup]="form">
      <customer-acceptance
        [parentForm]="form"
        [oldCustomer]="oldCustomer"
        [newCustomer]="newCustomer"
        fieldKey="CUSTOMER"></customer-acceptance>
    </form>`
})
class MockHostComponent {
  form: UntypedFormGroup;
  oldCustomer: Customer;
  newCustomer: Customer;

  constructor(fb: UntypedFormBuilder) {
    this.form = fb.group({});
  }
}

@Component({
  selector: 'customer-info-acceptance',
  template: ''
})
class MockCustomerInfoAcceptanceComponent {
  _oldCustomer: Customer;
  _newCustomer: Customer;

  @Input() form: UntypedFormGroup;
  @Input() countryCodes: CodeSetCodeMap;
  @Input() readonly: boolean;
  @Input() hideExisting: boolean;

  @Input() set oldCustomer(customer: Customer) {
    this._oldCustomer = customer;
  }

  @Input() set newCustomer(customer: Customer) {
    this._newCustomer = customer;
  }
}

@NgModule({
    imports: [AlluCommonModule],
    declarations: [CustomerModalComponent],
    providers: [
        { provide: CustomerService, useClass: CustomerServiceMock },
        { provide: NotificationService, useClass: NotificationServiceMock }
    ]
})
class DialogTestModule { }

const oldCustomer = new Customer(1, CustomerType.PERSON, 'old existing customer', 'oldKey');
const newCustomer = new Customer(undefined, CustomerType.PERSON, 'new shining customer', 'newKey');
const existingCustomer1 = new Customer(2, CustomerType.PERSON, 'first existing customer', '1key');
const existingCustomer2 = new Customer(3, CustomerType.PERSON, 'second existing customer', '2key');
const codeSet: CodeSetTypeMap = {
  Country: {
    FI: new CodeSet(1, 'Country', 'FI', 'Suomi')
  }
};

describe('CustomerAcceptanceComponent', () => {
  let hostComp: MockHostComponent;
  let fixture: ComponentFixture<MockHostComponent>;
  let de: DebugElement;
  let store: Store<fromCustomerSearch.State>;
  let dialogRef: MatDialogRefMock;
  let dialog: MatDialogMock;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule,
        DialogTestModule,
        StoreModule.forRoot({
          'customer': combineReducers(fromCustomerSearch.reducers),
          'codeSets': fromCodeSet.reducer
        }),
      ],
      declarations: [
        MockHostComponent,
        CustomerAcceptanceComponent,
        MockCustomerInfoAcceptanceComponent,
        CustomerOptionContentComponent
      ],
      providers: [
        UntypedFormBuilder,
        {provide: MatDialogRef, useClass: MatDialogRefMock},
        {provide: MatDialog, useClass: MatDialogMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockHostComponent);
    hostComp = fixture.componentInstance;
    de = fixture.debugElement;
    store = TestBed.inject(Store);
    dialog = TestBed.inject(MatDialog) as unknown as MatDialogMock;
    dialogRef = TestBed.inject(MatDialogRef) as MatDialogRefMock;

    hostComp.oldCustomer = oldCustomer;
    hostComp.newCustomer = newCustomer;

    store.dispatch(new SearchSuccess(ActionTargetType.Applicant, new Page([existingCustomer1, existingCustomer2])));
    store.dispatch(new CodeSetAction.LoadSuccess(codeSet));

    fixture.detectChanges();
  });

  it('should initialize', () => {
    expect(de.query(By.css('form'))).toBeDefined();
    expect(de.query(By.directive(MockCustomerInfoAcceptanceComponent))).toBeDefined();
  });

  it('should pass values to child component', () => {
    const childComp = de.query(By.directive(MockCustomerInfoAcceptanceComponent)).componentInstance;
    expect(childComp._newCustomer).toEqual(newCustomer);
    expect(childComp._oldCustomer).toEqual(oldCustomer);
    expect(childComp.countryCodes).toEqual(codeSet['Country']);
  });

  it('should pass old customer on change', () => {
    const testedComponent: CustomerAcceptanceComponent = de.query(By.directive(CustomerAcceptanceComponent)).componentInstance;
    const childComp = de.query(By.directive(MockCustomerInfoAcceptanceComponent)).componentInstance;
    testedComponent.selectReferenceCustomer(existingCustomer1);
    fixture.detectChanges();
    expect(childComp._oldCustomer).toEqual(existingCustomer1);
  });

  it('should do initial customer search when old customer is undefined', () => {
    hostComp.oldCustomer = undefined;
    fixture.detectChanges();
    const testedComponent: CustomerAcceptanceComponent = de.query(By.directive(CustomerAcceptanceComponent)).componentInstance;
    expect(testedComponent.oldCustomer).toBeUndefined();

    testedComponent.ngOnInit();
    store.dispatch(new SearchSuccess(ActionTargetType.Applicant, new Page([existingCustomer1, existingCustomer2])));
    fixture.detectChanges();

    const childComp = de.query(By.directive(MockCustomerInfoAcceptanceComponent)).componentInstance;
    expect(childComp._oldCustomer).toEqual(existingCustomer1);
  });

  it('should not do initial search when existing old customer', () => {
    spyOn(store, 'dispatch').and.callThrough();
    fixture.detectChanges();
    expect(store.dispatch).not.toHaveBeenCalled();
  });

  it('should create new customer by user selection', () => {
    const childComp = de.query(By.directive(MockCustomerInfoAcceptanceComponent)).componentInstance;
    const testedComponent: CustomerAcceptanceComponent = de.query(By.directive(CustomerAcceptanceComponent)).componentInstance;

    expect(childComp._oldCustomer).toEqual(oldCustomer);

    spyOn(dialog, 'open').and.returnValue(dialogRef);
    spyOn(dialogRef, 'afterClosed').and.returnValue(of(newCustomer));

    testedComponent.createNewCustomer();
    fixture.detectChanges();
    expect(childComp._oldCustomer).toEqual(newCustomer);
  });
});
