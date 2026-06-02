import {CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA} from '@angular/core';
import {waitForAsync, ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {Store, StoreModule} from '@ngrx/store';
import {of} from 'rxjs/index';

import {CustomerComponent} from '@feature/customerregistry/customer/customer.component';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Clear, LoadByTargetId} from '@feature/history/actions/history-actions';
import {reducers as customerReducers} from '@feature/customerregistry/reducers';
import {CustomerService} from '@service/customer/customer.service';
import {NotificationService} from '@feature/notification/notification.service';
import {CurrentUser} from '@service/user/current-user';
import {Customer} from '@model/customer/customer';
import {ActivatedRouteMock, CurrentUserMock, NotificationServiceMock, RouterMock} from 'test/mocks';
import {CustomerType} from '@model/customer/customer-type';
import {reducers as rootReducers} from '@feature/allu/reducers';

class CustomerServiceMock {
  findCustomerById(id: number) {
    return of(new Customer(id, CustomerType.COMPANY, 'Testi Oy'));
  }

  saveCustomerWithContacts() {
    return of(undefined);
  }
}

describe('CustomerComponent', () => {
  let fixture: ComponentFixture<CustomerComponent>;
  let component: CustomerComponent;
  let store: Store;
  let route: ActivatedRouteMock;
  let dispatchSpy: jasmine.Spy;
  let customerService: CustomerServiceMock;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        StoreModule.forRoot(rootReducers),
        StoreModule.forFeature('customer', customerReducers)
      ],
      declarations: [CustomerComponent],
      providers: [
        {provide: ActivatedRoute, useClass: ActivatedRouteMock},
        {provide: Router, useClass: RouterMock},
        {provide: CustomerService, useClass: CustomerServiceMock},
        {provide: NotificationService, useClass: NotificationServiceMock},
        {provide: CurrentUser, useClass: CurrentUserMock}
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(Store);
    route = TestBed.inject(ActivatedRoute) as unknown as ActivatedRouteMock;
    customerService = TestBed.inject(CustomerService) as unknown as CustomerServiceMock;
    dispatchSpy = spyOn(store, 'dispatch');
    spyOn(customerService, 'findCustomerById').and.callThrough();
  });

  it('should clear customer history when route id is missing', () => {
    fixture.detectChanges();

    expect(dispatchSpy).toHaveBeenCalledWith(new Clear(ActionTargetType.Customer));
    expect(component.hasCustomerId).toBeFalse();
  });

  it('should load customer history when route id exists', () => {
    route.testParams = {id: '3'};
    fixture.detectChanges();

    expect(dispatchSpy).toHaveBeenCalledWith(new LoadByTargetId(ActionTargetType.Customer, 3));
    expect(customerService.findCustomerById).toHaveBeenCalledWith(3);
    expect(component.hasCustomerId).toBeTrue();
  });
});
