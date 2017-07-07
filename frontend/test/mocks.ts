import {Directive} from '@angular/core';
import {MetadataOverride} from '@angular/core/testing';
import {Observable} from 'rxjs/Observable';
import {CurrentUser} from '../src/service/user/current-user';

export class CurrentUserMock {
  public allowHasRole = true;
  public allowHasType = true;

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

