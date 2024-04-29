import {TestBed} from '@angular/core/testing';
import {ActivatedRouteSnapshot} from '@angular/router';
import {REDIRECT_URL} from '../../../util/local-storage';
import {User} from '@model/user/user';
import {UiConfiguration} from '@model/config/ui-configuration';
import {AuthGuard} from '@service/authorization/auth-guard.service';
import {AuthService} from '@service/authorization/auth.service';
import {ConfigService} from '@service/config/config.service';
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

const activatedRouteSnapshot = new ActivatedRouteSnapshot();
const routerStateSnapshot = {url: 'default', root: undefined};

describe('AuthGuard', () => {
  let authGuard: AuthGuard;
  let authService: AuthServiceMock;
  let configService: ConfigServiceMock;

  beforeEach(() => {
    const store = {};

    spyOn(localStorage, 'getItem').and.callFake( (key: string): string => {
      return store[key] || null;
    });
    spyOn(localStorage, 'removeItem').and.callFake((key: string): void =>  {
      delete store[key];
    });
    spyOn(localStorage, 'setItem').and.callFake((key: string, value: string): string =>  {
      return store[key] = <string>value;
    });
    const tb = TestBed.configureTestingModule({
      imports: [],
      providers: [
        {provide: AuthService, useClass: AuthServiceMock},
        {provide: ConfigService, useClass: ConfigServiceMock},
        AuthGuard
      ]
    });
    authGuard = tb.inject(AuthGuard);
    authService = tb.inject(AuthService) as AuthServiceMock;
    configService = tb.inject(ConfigService) as ConfigServiceMock;
  });

  it('allows route activation when already authenticated', () => {
    spyOn(authService, 'authenticated').and.returnValue(true);
    authGuard.canActivate(activatedRouteSnapshot, routerStateSnapshot)
      .subscribe(canActivate => expect(canActivate).toEqual(true));
  });

  it('authenticates when code is found in route parameters', () => {
    spyOn(authService, 'authenticated').and.returnValue(false);
    const loginOauth = spyOn(authService, 'loginOAuth').and.returnValue(of(new User()));
    activatedRouteSnapshot.queryParams = {code: 'CODE'};

    authGuard.canActivate(activatedRouteSnapshot, routerStateSnapshot).subscribe();
    expect(loginOauth).toHaveBeenCalledWith('CODE');
  });

  it('redirects to oauth login when no code is found in route parameters', () => {
    spyOn(authService, 'authenticated').and.returnValue(false);
    const getConfiguration = spyOn(configService, 'getConfiguration').and.returnValue(EMPTY);
    activatedRouteSnapshot.queryParams = {code: undefined};
    routerStateSnapshot.url = 'testUrl';

    authGuard.canActivate(activatedRouteSnapshot, routerStateSnapshot).subscribe();
    expect(localStorage.getItem(REDIRECT_URL)).toBe(routerStateSnapshot.url);
    expect(getConfiguration).toHaveBeenCalled();
  });
});
