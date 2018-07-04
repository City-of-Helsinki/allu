import {Component, DebugElement, Input} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Customer} from '../../../../src/app/model/customer/customer';
import {CodeSet, CodeSetCodeMap, CodeSetTypeMap} from '../../../../src/app/model/codeset/codeset';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromCustomerSearch from '../../../../src/app/feature/customerregistry/reducers';
import * as fromCodeSet from '../../../../src/app/feature/allu/reducers/code-set-reducer';
import {AlluCommonModule} from '../../../../src/app/feature/common/allu-common.module';
import {CustomerAcceptanceComponent} from '../../../../src/app/feature/information-request/acceptance/customer-acceptance.component';
import {SearchSuccess} from '../../../../src/app/feature/customerregistry/actions/customer-search-actions';
import * as CodeSetAction from '../../../../src/app/feature/allu/actions/code-set-actions';
import {By} from '@angular/platform-browser';

@Component({
  selector: 'host',
  template: `
    <form [formGroup]="form">
      <customer-acceptance
        [parentForm]="form"
        [oldCustomer]="oldCustomer"
        [newCustomer]="newCustomer"></customer-acceptance>
    </form>`
})
class MockHostComponent {
  form: FormGroup;
  oldCustomer: Customer;
  newCustomer: Customer;

  constructor(fb: FormBuilder) {
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

  @Input() form: FormGroup;
  @Input() countryCodes: CodeSetCodeMap;

  @Input() set oldCustomer(customer: Customer) {
    this._oldCustomer = customer;
  }

  @Input() set newCustomer(customer: Customer) {
    this._newCustomer = customer;
  }
}

const oldCustomer = new Customer(1, 'PERSON', 'old existing customer', 'oldKey');
const newCustomer = new Customer(undefined, 'PERSON', 'new shining customer', 'newKey');
const existingCustomer1 = new Customer(2, 'PERSON', 'first existing customer', '1key');
const existingCustomer2 = new Customer(3, 'PERSON', 'second existing customer', '2key');
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

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule,
        StoreModule.forRoot({
          'customer': combineReducers(fromCustomerSearch.reducers),
          'codeSets': fromCodeSet.reducer
        }),
      ],
      declarations: [
        MockHostComponent,
        CustomerAcceptanceComponent,
        MockCustomerInfoAcceptanceComponent
      ],
      providers: [
        FormBuilder
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockHostComponent);
    hostComp = fixture.componentInstance;
    de = fixture.debugElement;
    store = TestBed.get(Store);

    hostComp.oldCustomer = oldCustomer;
    hostComp.newCustomer = newCustomer;

    store.dispatch(new SearchSuccess([existingCustomer1, existingCustomer2]));
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
    store.dispatch(new SearchSuccess([existingCustomer1, existingCustomer2]));
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
    const buttonElem: HTMLButtonElement = de.query(By.css('button.button-link')).nativeElement;

    expect(childComp._oldCustomer).toEqual(oldCustomer);

    buttonElem.click();
    fixture.detectChanges();
    expect(childComp._oldCustomer).toBeUndefined();
  });
});
