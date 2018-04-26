import {Injectable} from '@angular/core';
import {Headers, Http, URLSearchParams} from '@angular/http';
import {JwtHelper} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {User} from '../../model/user/user';
import {CurrentUser} from '../user/current-user';
import * as fromAuth from '../../feature/auth/reducers';
import {Store} from '@ngrx/store';
import {LoggedIn, LoggedOut} from '../../feature/auth/actions/auth-actions';

const LOGIN_URL = '/api/auth/login';
const OAUTH_URL = '/api/oauth2/';

@Injectable()
export class AuthService {
  private jwtHelper: JwtHelper = new JwtHelper();
  private contentHeaders = new Headers();

  constructor(private http: Http, private currentUser: CurrentUser, private store: Store<fromAuth.State>) {
    this.contentHeaders.append('Accept', 'application/json');
    this.contentHeaders.append('Content-Type', 'application/json');
  }

  authenticated(): boolean {
    const jwt = localStorage.getItem('jwt');
    return !!jwt && !this.jwtHelper.isTokenExpired(jwt);
  }

  login(username: string): Observable<User> {
    const body = JSON.stringify({ 'userName': username });
    return this.http.post(LOGIN_URL, body, { headers: this.contentHeaders })
      .switchMap(response => this.loggedIn(response.text()));
  }

  loginOAuth(code: string): Observable<User> {
    const searchParams = new URLSearchParams();
    searchParams.append('code', code);
    return this.http.get(OAUTH_URL, {headers: this.contentHeaders, search: searchParams})
      .switchMap(response => this.loggedIn(response.text()));
  }

  logout(): void {
    localStorage.removeItem('jwt');
    this.currentUser.clearUser();
    this.store.dispatch(new LoggedOut());
  }

  get token(): string {
    return localStorage.getItem('jwt');
  }

  private storeJwt(token: string): void {
    localStorage.setItem('jwt', token);
  }

  private loggedIn(jwt: string): Observable<User> {
    this.storeJwt(jwt);
    this.store.dispatch(new LoggedIn());
    return this.currentUser.user;
  }
}
