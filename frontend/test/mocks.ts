import {Directive} from '@angular/core';
import {MetadataOverride} from '@angular/core/testing';
import {Observable} from 'rxjs/Observable';
import {CurrentUser} from '../src/service/user/current-user';
import {Application} from '../src/model/application/application';
import {Contact} from '../src/model/customer/contact';
import {Subject} from 'rxjs/Subject';
import {User} from '../src/model/user/user';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Location} from '../src/model/common/location';
import {RoleType} from '../src/model/user/role-type';
import {NavigationExtras} from '@angular/router';

export class ApplicationStateMock {
  private _application: Application;

  constructor() {
    this._application = new Application(1);
    let location = new Location(1);
    location.cityDistrictId = 1;
    this._application.locations.push(location);
  }

  get application() {
    return this._application;
  }
}

export class CustomerHubMock {
  public orderer$ = new Subject<Contact>();

  searchCustomersByField(fieldName: string, term: string) {}
  findCustomerActiveContacts(customerId: number) {};
  get orderer() { return this.orderer$.asObservable(); };
  ordererWasSelected(orderer) {};
}

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

export class UserHubMock {
  public getByRole = (role: RoleType) => Observable.of([supervisor]);
}

export class RouterMock {
  public navigate(commands: any[], extras?: NavigationExtras): Promise<boolean> {
    return Promise.resolve(true);
  }
};

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

