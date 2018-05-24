import {TestBed} from '@angular/core/testing';
import {Observable} from 'rxjs/Observable';
import {ActivatedRouteSnapshot} from '@angular/router';
import {REDIRECT_URL} from '../../../src/util/local-storage';
import {User} from '../../../src/app/model/user/user';
import {UiConfiguration} from '../../../src/app/model/config/ui-configuration';
import {AuthGuard} from '../../../src/app/service/authorization/auth-guard.service';
import {AuthService} from '../../../src/app/service/authorization/auth.service';
import {ConfigService} from '../../../src/app/service/config/config.service';
import {EMPTY, of} from 'rxjs/index';

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
    const tb = TestBed.configureTestingModule({
      imports: [],
      providers: [
        {provide: AuthService, useClass: AuthServiceMock},
        {provide: ConfigService, useClass: ConfigServiceMock},
        AuthGuard
      ]
    });
    authGuard = tb.get(AuthGuard);
    authService = tb.get(AuthService) as AuthServiceMock;
    configService = tb.get(ConfigService) as ConfigServiceMock;
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
    const setItem = spyOn(localStorage, 'setItem');
    activatedRouteSnapshot.queryParams = {code: undefined};
    routerStateSnapshot.url = 'testUrl';

    authGuard.canActivate(activatedRouteSnapshot, routerStateSnapshot).subscribe();
    expect(setItem).toHaveBeenCalledWith(REDIRECT_URL, routerStateSnapshot.url);
    expect(getConfiguration).toHaveBeenCalled();
  });
});
