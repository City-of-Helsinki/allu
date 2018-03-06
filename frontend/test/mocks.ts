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
import {ApplicationState} from '../src/app/service/application/application-store';
import {Comment} from '../src/app/model/application/comment/comment';
import {ApplicationType} from '../src/app/model/application/type/application-type';
import {CityDistrict} from '../src/app/model/common/city-district';

/**
 * Mock for application state
 */
export class ApplicationStoreMock {
  private application$ = new BehaviorSubject(new Application());
  private applicationCopy$ =  new BehaviorSubject(undefined);
  public comments$ = new Subject<Array<Comment>>();

  constructor() {
    const application = new Application(1);
    application.statusEnum = ApplicationStatus.PENDING;
    const location = new Location(1);
    location.cityDistrictId = 1;
    application.locations.push(location);
    this.application$.next(application);
  }

  get snapshot(): ApplicationState {
    return {application: this.application$.getValue(), applicationCopy: this.applicationCopy$.getValue()};
  }

  get application() {
    return this.application$.asObservable();
  }

  applicationChange(value: Application) {
    this.application$.next(value);
  }

  get changes(): Observable<ApplicationState> {
    return this.application$.map(app => ({application: app}));
  }

  applicationCopyChange(app: Application) {
    this.applicationCopy$.next(app);
  }

  delete(id: number): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }

  replace(): Observable<Application> {
    return Observable.empty();
  }

  changeStatus(id: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    this.applicationChange(this.application$.getValue());
    return Observable.of(this.snapshot.application);
  }

  get comments() { return this.comments$.asObservable(); }

  saveComment(applicationId: number, comment: Comment) {}

  removeComment(comment: Comment) {}

  // Testing helper which updates status of application and emits new application
  updateStatus(status: ApplicationStatus): void {
    const app = this.application$.getValue();
    app.statusEnum = status;
    this.applicationChange(app);
  }

  // Testing helper which updates type of application and emits new application
  updateType(type: ApplicationType): void {
    const app = this.application$.getValue();
    app.type = ApplicationType[type];
    this.applicationChange(app);
  }
}

/**
 * Mock for customer hub
 */
export class CustomerServiceMock {
  public orderer$ = new Subject<Contact>();

  searchCustomersByField(fieldName: string, term: string) {}
  findCustomerActiveContacts(customerId: number) {}
  get orderer() { return this.orderer$.asObservable(); }
  ordererWasSelected(orderer) {}
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
  }

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
}

export class ActivatedRouteMock {
  private params$ = new BehaviorSubject({});
  private data$ = new BehaviorSubject({});

  params  = this.params$.asObservable();
  data = this.data$.asObservable();

  set testParams(newParams: any) {
    this.params$.next(newParams);
  }

  set testData(data: any) {
    this.data$.next(data);
  }
}

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

export class CityDistrictServiceMock {
  private districts = [
    new CityDistrict(1, 1, 'First'),
    new CityDistrict(2, 2, 'Second')
  ];

  public get(): Observable<CityDistrict[]> {
    return Observable.of(this.districts);
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
}

