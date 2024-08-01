/*
todo: restore tests after the occasionally failing test has been fixed
import {TestBed} from '@angular/core/testing';
import {ActivatedRouteSnapshot} from '@angular/router';
import {REDIRECT_URL} from '../../../util/local-storage';
import {User} from '@model/user/user';
import {UiConfiguration} from '@model/config/ui-configuration';
import {AuthGuard} from '@service/authorization/auth-guard.service';
import {AdminGuard} from '@app/service/authorization/admin-guard.service';
import {AuthService} from '@service/authorization/auth.service';
import {ConfigService} from '@service/config/config.service';
import {CurrentUser} from '@app/service/user/current-user';
import {EMPTY, Observable, of} from 'rxjs';

class AuthServiceMock {
  authenticated(): boolean {
    return true;
  }

  loginOAuth(code: string): Observable<User> {
    return EMPTY;
  }
}

class ConfigServiceMock {
  getConfiguration(): Observable<UiConfiguration> {
    return EMPTY;
  }
}

export class CurrentUserMock {

  constructor(public allowHasRole = true, public allowHasType = true) {}

  public static create(allowHasRole: boolean, allowHasType: boolean) {
    const mock = new CurrentUserMock();
    mock.allowHasRole = allowHasRole;
    mock.allowHasType = allowHasType;
    return mock;
  }

  public hasOnlyView(roles: Array<string>): Observable<boolean> {
    return of(this.allowHasRole);
  }
}

const activatedRouteSnapshot = new ActivatedRouteSnapshot();
const routerStateSnapshot = {url: 'default', root: undefined};

describe('AdminGuard', () => {
  let adminGuard: AdminGuard;
  let authService: AuthServiceMock;
  let configService: ConfigServiceMock;

  beforeEach(() => {

    const tb = TestBed.configureTestingModule({
      imports: [],
      providers: [
        {provide: AuthService, useClass: AuthServiceMock},
        {provide: ConfigService, useClass: ConfigServiceMock},
        {provide: CurrentUser, useClass: CurrentUserMock},
        AdminGuard
      ]
    });
    adminGuard = tb.inject(AdminGuard);
    authService = tb.inject(AuthService) as AuthServiceMock;
    configService = tb.inject(ConfigService) as ConfigServiceMock;
  });

  it('deny route activation when not admin', () => {
    spyOn(authService, 'authenticated').and.returnValue(true);
    adminGuard.canActivate(activatedRouteSnapshot, routerStateSnapshot)
      .subscribe(canActivate => expect(canActivate).toEqual(false));
  });

  it('authenticates when code is found in route parameters', () => {
    spyOn(authService, 'authenticated').and.returnValue(false);
    const loginOauth = spyOn(authService, 'loginOAuth').and.returnValue(of(new User()));
    activatedRouteSnapshot.queryParams = {code: 'CODE'};

    adminGuard.canActivate(activatedRouteSnapshot, routerStateSnapshot).subscribe();
    expect(loginOauth).toHaveBeenCalledWith('CODE');
  });

  it('redirects to oauth login when no code is found in route parameters', () => {
    spyOn(authService, 'authenticated').and.returnValue(false);
    const getConfiguration = spyOn(configService, 'getConfiguration').and.returnValue(EMPTY);
    activatedRouteSnapshot.queryParams = {code: undefined};
    routerStateSnapshot.url = 'testUrl';

    adminGuard.canActivate(activatedRouteSnapshot, routerStateSnapshot).subscribe();
    expect(localStorage.getItem(REDIRECT_URL)).toBe(routerStateSnapshot.url);
    expect(getConfiguration).toHaveBeenCalled();
  });
});
*/