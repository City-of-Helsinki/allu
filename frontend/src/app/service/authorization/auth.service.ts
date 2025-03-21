import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {User} from '../../model/user/user';
import {CurrentUser} from '../user/current-user';
import * as fromAuth from '../../feature/auth/reducers';
import {Store} from '@ngrx/store';
import {LoggedIn, LoggedOut} from '../../feature/auth/actions/auth-actions';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {JwtHelperService} from '@auth0/angular-jwt';
import {switchMap, tap} from 'rxjs/internal/operators';

const LOGIN_URL = '/api/auth/login';
const OAUTH_URL = '/api/oauth2/';

@Injectable()
export class AuthService {
  private contentHeaders = new HttpHeaders({
    'Accept': 'application/json',
    'Content-Type': 'application/json'
  });

  private jwtHelper: JwtHelperService = new JwtHelperService();
  private authCheckedTimestamp: number = 0;
  private cachedAuthResult: boolean = false;

  constructor(private http: HttpClient,
              private currentUser: CurrentUser,
              private store: Store<fromAuth.State>) {
  }

  authenticated(): boolean {
    const currentTime = new Date().getTime();

    //cache auth result
    if (currentTime - this.authCheckedTimestamp < 1000) {
      console.log('LOGINBUG auth.service.authenticated using cached result', this.cachedAuthResult);
      return this.cachedAuthResult;
    }

    const jwt = sessionStorage.getItem('jwt');

    console.log('LOGINBUG auth.service.authenticated jwt exists', !!jwt);

    this.cachedAuthResult = !!jwt && !this.jwtHelper.isTokenExpired(jwt);
    this.authCheckedTimestamp = currentTime;
    console.log('LOGINBUG auth.service.authenticated result', this.cachedAuthResult);

    return this.cachedAuthResult;
  }

  login(username: string): Observable<User> {
    console.log('LOGINBUG auth.service.login username', username);

    const body = JSON.stringify({ 'userName': username });
    return this.http.post(LOGIN_URL, body, { headers: this.contentHeaders, responseType: 'text' }).pipe(
      tap(jwt => console.log('LOGINBUG auth.service.login JWT received length', jwt?.length)),
      switchMap(jwt => this.loggedIn(jwt))
    );
  }

  loginOAuth(code: string): Observable<User> {
    console.log('LOGINBUG auth.service.loginOAuth code present get jwt');

    const params = new HttpParams().set('code', code);
    return this.http.get(OAUTH_URL, {headers: this.contentHeaders, params: params, responseType: 'text'}).pipe(
      tap(jwt => console.log('LOGINBUG auth.service.loginOAuth JWT received length', jwt?.length)),
      switchMap(jwt => this.loggedIn(jwt))
    );
  }

  logout(): void {
    console.log('LOGINBUG auth.service.logout clearing session data');

    sessionStorage.removeItem('jwt');
    this.currentUser.clearUser();
    this.store.dispatch(new LoggedOut());
    this.cachedAuthResult = false;
  }

  get token(): string {
    return sessionStorage.getItem('jwt');
  }

  private storeJwt(token: string): void {
    console.log('LOGINBUG auth.service.storeJWT storing JWT in sessionStorage');

    sessionStorage.setItem('jwt', token);
  }

  private loggedIn(jwt: string): Observable<User> {
    console.log('LOGINBUG auth.service.loggedIn processing login');

    this.storeJwt(jwt);
    this.store.dispatch(new LoggedIn());
    this.cachedAuthResult = true;
    return this.currentUser.user;
  }
}
