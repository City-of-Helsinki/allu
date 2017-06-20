import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate} from '@angular/router';
import {JwtHelper} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {AuthService} from './auth.service';

// TODO: These should be fetched from backend?
const OAUTH_URL = 'https://fs.hel.fi/adfs/oauth2/authorize?response_type=code';
const CLIENT_ID = 'c0fa3298-6e1a-428c-abed-c6197f8fa559';
const REDIRECT_URL = 'http://185.26.49.172/oauth2/';

@Injectable()
export class AuthGuard implements CanActivate {
  private jwtHelper: JwtHelper;

  constructor(private authService: AuthService) {
    this.jwtHelper = new JwtHelper();
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    if (this.authService.authenticated()) {
      return Observable.of(true);
    } else {
      return this.authenticate(route);
    }
  }

  private authenticate(route: ActivatedRouteSnapshot): Observable<boolean> {
    let code = route.queryParams['code'];
    if (code) {
      return this.authService.loginOAuth(code).map(response => true);
    } else {
      this.redirectToOAuth();
      return Observable.of(false);
    }
  }

  private redirectToOAuth() {
    let uri = OAUTH_URL
      + this.asParam('client_id', CLIENT_ID)
      + this.asParam('redirect_uri', encodeURIComponent(REDIRECT_URL))
      + this.asParam('resource', encodeURIComponent(REDIRECT_URL));

    window.location.href = uri;
  }

  private asParam(name: string, value: string): string {
    return '&' + name + '=' + value;
  }
}
