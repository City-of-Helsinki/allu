import {Directive} from '@angular/core';
import {MetadataOverride} from '@angular/core/testing';
import {Observable} from 'rxjs/Observable';
import {Application} from '../src/app/model/application/application';
import {Location} from '../src/app/model/common/location';
import {HttpResponse, HttpStatus} from '../src/app/util/http-response';
import {ApplicationStatus} from '../src/app/model/application/application-status';
import {StatusChangeInfo} from '../src/app/model/application/status-change-info';
import {Subject} from 'rxjs/Subject';
import {Contact} from '../src/app/model/customer/contact';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {User} from '../src/app/model/user/user';
import {RoleType} from '../src/app/model/user/role-type';
import {NavigationExtras, UrlTree} from '@angular/router';
import {ChargeBasisEntry} from '../src/app/model/application/invoice/charge-basis-entry';
import {CurrentUser} from '../src/app/service/user/current-user';

/**
 * Mock for application state
 */
export class ApplicationStateMock {
  public _application: Application;

  constructor() {
    this._application = new Application(1);
    let location = new Location(1);
    location.cityDistrictId = 1;
    this._application.locations.push(location);
  }

  get application() {
    return this._application;
  }

  set application(value: Application) {
    this._application = value;
  }

  get changes(): Observable<Application> {
    return Observable.of(this._application);
  }

  set applicationCopy(app: Application) {
    this._application = app;
  }

  delete(id: number): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }

  changeStatus(id: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    return Observable.of(this._application);
  }
}

/**
 * Mock for customer hub
 */
export class CustomerHubMock {
  public orderer$ = new Subject<Contact>();

  searchCustomersByField(fieldName: string, term: string) {}
  findCustomerActiveContacts(customerId: number) {};
  get orderer() { return this.orderer$.asObservable(); };
  ordererWasSelected(orderer) {};
}

/**
 * Mock for Current user service
 */
export class CurrentUserMock {
  public allowHasRole = true;
  public allowHasType = true;
  public user$ = new BehaviorSubject(new User(1));

  public static create(allowHasRole: boolean, allowHasType: boolean) {
    const mock = new CurrentUserMock();
    mock.allowHasRole = allowHasRole;
    mock.allowHasType = allowHasType;
    return mock;
  }

  public hasRole(roles: Array<string>): Observable<boolean> {
    return Observable.of(this.allowHasRole);
  }

  public hasApplicationType(types: Array<string>): Observable<boolean> {
    return Observable.of(this.allowHasType);
  }

  public isCurrentUser(id: number): Observable<boolean> {
    return Observable.of(true);
  };

  get user(): Observable<User> {
    return this.user$.asObservable();
  }
}

const supervisor = new User(1, 'supervisor', 'super visor');

/**
 * Mock for user hub
 */
export class UserHubMock {
  public getByRole = (role: RoleType) => Observable.of([supervisor]);
}

/**
 * Mock for angular's router
 */
export class RouterMock {
  public navigate(commands: any[], extras?: NavigationExtras): Promise<boolean> {
    return Promise.resolve(true);
  }

  navigateByUrl(url: string | UrlTree, extras?: NavigationExtras): Promise<boolean> {
    return Promise.resolve(true);
  }
};

/**
 * Mock for InvoiceHub
 */
export class InvoiceHubMock {
  public loadChargeBasisEntries(applicationId: number): Observable<Array<ChargeBasisEntry>> {
    return Observable.empty();
  }

  public saveChargeBasisEntries(applicationId: number, rows: Array<ChargeBasisEntry>): Observable<Array<ChargeBasisEntry>> {
    return Observable.empty();
  }

  get chargeBasisEntries(): Observable<Array<ChargeBasisEntry>> {
    return Observable.empty();
  }
}

/**
 * Function to create mock metadata which can be used to override directive in test components
 *
 * @param mock mocked CurrentUser-service to allow controlling access rights
 * @returns MetadataOverride<Directive> override metadata
 */
export function availableToDirectiveMockMeta(mock: CurrentUserMock = new CurrentUserMock()): MetadataOverride<Directive> {
  return {
    set: {
      providers: [
        {provide: CurrentUser, useValue: mock}
      ]
    }
  };
};

