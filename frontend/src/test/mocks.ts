import { Component, Directive, Injectable } from '@angular/core';
import {MetadataOverride} from '@angular/core/testing';
import {Application} from '../app/model/application/application';
import {Location} from '../app/model/common/location';
import {ApplicationStatus} from '../app/model/application/application-status';
import {StatusChangeInfo} from '../app/model/application/status-change-info';
import {Contact} from '../app/model/customer/contact';
import {User} from '../app/model/user/user';
import {RoleType} from '../app/model/user/role-type';
import {NavigationExtras, UrlTree} from '@angular/router';
import {ChargeBasisEntry} from '../app/model/application/invoice/charge-basis-entry';
import {CurrentUser} from '../app/service/user/current-user';
import {ApplicationState} from '../app/service/application/application-store';
import {Comment} from '../app/model/application/comment/comment';
import {ApplicationType} from '../app/model/application/type/application-type';
import {CityDistrict} from '../app/model/common/city-district';
import {ErrorInfo} from '../app/service/error/error-info';
import {BehaviorSubject, EMPTY, Observable, of, Subject, throwError} from 'rxjs';
import {map} from 'rxjs/operators';
import {UserSearchCriteria} from '@model/user/user-search-criteria';

/**
 * Mock for application state
 */
export class ApplicationStoreMock {
  private application$ = new BehaviorSubject(new Application());
  private applicationCopy$ =  new BehaviorSubject(undefined);
  public comments$ = new Subject<Array<Comment>>();

  constructor() {
    const application = new Application(1);
    application.status = ApplicationStatus.PENDING;
    const location = new Location(1);
    location.cityDistrictId = 1;
    application.locations.push(location);
    this.application$.next(application);
  }

  get snapshot(): ApplicationState {
    return {application: this.application$.getValue(), applicationCopy: this.applicationCopy$.getValue(), replacedDisableRemoveButton: false};
  }

  get application() {
    return this.application$.asObservable();
  }

  applicationChange(value: Application) {
    this.application$.next(value);
  }

  get changes(): Observable<ApplicationState> {
    return this.application$.pipe(map(app => ({application: app, replacedDisableRemoveButton: false})));
  }

  applicationCopyChange(app: Application) {
    this.applicationCopy$.next(app);
  }

  delete(_id: number): Observable<unknown> {
    return of({});
  }

  replace(): Observable<Application> {
    return EMPTY;
  }

  changeStatus(_id: number, _status: ApplicationStatus, _changeInfo?: StatusChangeInfo): Observable<Application> {
    this.applicationChange(this.application$.getValue());
    return of(this.snapshot.application);
  }

  get comments() { return this.comments$.asObservable(); }

  saveComment(_applicationId: number, _comment: Comment) {}

  removeComment(_comment: Comment) {}

  // Testing helper which updates status of application and emits new application
  updateStatus(status: ApplicationStatus): void {
    const app = this.application$.getValue();
    app.status = status;
    this.applicationChange(app);
  }

  // Testing helper which updates type of application and emits new application
  updateType(type: ApplicationType): void {
    const app = this.application$.getValue();
    app.type = ApplicationType[type];
    this.applicationChange(app);
  }
}


@Injectable()
export class CustomerServiceMock {
  public orderer$ = new Subject<Contact>();

  searchCustomersByField(_fieldName: string, _term: string) {}
  findCustomerActiveContacts(_customerId: number) {}
  get orderer() { return this.orderer$.asObservable(); }
  ordererWasSelected(_orderer) {}
}

export class ContactServiceMock {
  public findById(id: number): Observable<Contact> {
    return of(new Contact(id));
  }

  public save(customerId: number, contact: Contact): Observable<Contact> {
    return of({...contact, customerId});
  }
}

/**
 * Mock for Current user service
 */
export class CurrentUserMock {
  public user$ = new BehaviorSubject(new User(1));

  constructor(public allowHasRole = true, public allowHasType = true) {}

  public static create(allowHasRole: boolean, allowHasType: boolean) {
    const mock = new CurrentUserMock();
    mock.allowHasRole = allowHasRole;
    mock.allowHasType = allowHasType;
    return mock;
  }

  public hasRole(_roles: Array<string>): Observable<boolean> {
    return of(this.allowHasRole);
  }

  public hasApplicationType(_types: Array<string>): Observable<boolean> {
    return of(this.allowHasType);
  }

  public isCurrentUser(_id: number): Observable<boolean> {
    return of(true);
  }

  get user(): Observable<User> {
    return this.user$.asObservable();
  }
}

const supervisor = new User(1, 'supervisor', 'super visor');

/**
 * Mock for user service
 */
export class UserServiceMock {
  public getCurrentUser(): Observable<User> {
    return of(new User(1));
  }
  public getByRole = (_role: RoleType) => of([supervisor]);

  public search(_criteria: UserSearchCriteria) { return of([]); }

  public getAllUsers(): Observable<User[]> {
    return of([]);
  }
}

/**
 * Mock for angular's router
 */
export class RouterMock {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  public navigate(_commands: any[], _extras?: NavigationExtras): Promise<boolean> {
    return Promise.resolve(true);
  }

  navigateByUrl(_url: string | UrlTree, _extras?: NavigationExtras): Promise<boolean> {
    return Promise.resolve(true);
  }
}


export class ActivatedRouteMock {
  private params$ = new BehaviorSubject({});
  private data$ = new BehaviorSubject({});

  params  = this.params$.asObservable();
  data = this.data$.asObservable();

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  set testParams(newParams: any) {
    this.params$.next(newParams);
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  set testData(data: any) {
    this.data$.next(data);
  }
}

@Component({
  selector: 'mock-routed',
  template: `<div>empty</div>`
})
export class MockRoutedComponent {}

/**
 * Mock for InvoiceHub
 */
export class InvoiceHubMock {
  public loadChargeBasisEntries(_applicationId: number): Observable<Array<ChargeBasisEntry>> {
    return EMPTY;
  }

  public saveChargeBasisEntries(_applicationId: number, _rows: Array<ChargeBasisEntry>): Observable<Array<ChargeBasisEntry>> {
    return EMPTY;
  }

  get chargeBasisEntries(): Observable<Array<ChargeBasisEntry>> {
    return EMPTY;
  }
}

export class CityDistrictServiceMock {
  private districts = [
    new CityDistrict(1, 1, 'First'),
    new CityDistrict(2, 2, 'Second')
  ];

  public get(): Observable<CityDistrict[]> {
    return of(this.districts);
  }
}

@Injectable()
export class NotificationServiceMock {
  translateSuccess(_key: string): void {}

  success(_title: string, _message?: string): void {}

  info(_title: string, _message?: string): void  {}

  error(_title: string, _message?: string): void {}

  errorInfo(_errorInfo: ErrorInfo): void {}

  errorCatch<T>(errorInfo: ErrorInfo, returnValue?: T): Observable<T> {
    return returnValue ? of(returnValue) : EMPTY;
  }

  translateError(_errorInfo: ErrorInfo): void {}

  translateErrorMessage(_key: string): void {}
}

export class ErrorHandlerMock {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  handle(error: any, message?: string): Observable<any> {
    return throwError(new ErrorInfo('error', message));
  }
}

/**
 * Function to create mock metadata which can be used to override directive in test components
 *
 * @param mock mocked CurrentUser-service to allow controlling access rights
 * @returns MetadataOverride<Directive> override metadata
 */
export function availableToDirectiveMockMeta(mock: CurrentUserMock = new CurrentUserMock(true, true)): MetadataOverride<Directive> {
  return {
    set: {
      providers: [
        {provide: CurrentUser, useValue: mock}
      ]
    }
  };
}

export class MatDialogMock {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  open(): any {
    return undefined;
  }
}

export class MatDialogRefMock {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  afterClosed(): Observable<any> {
    return of();
  }
}
