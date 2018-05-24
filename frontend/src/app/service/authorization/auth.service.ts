import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {User} from '../../model/user/user';
import {CurrentUser} from '../user/current-user';
import * as fromAuth from '../../feature/auth/reducers';
import {Store} from '@ngrx/store';
import {LoggedIn, LoggedOut} from '../../feature/auth/actions/auth-actions';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {JwtHelperService} from '@auth0/angular-jwt';
import {switchMap} from 'rxjs/internal/operators';

const LOGIN_URL = '/api/auth/login';
const OAUTH_URL = '/api/oauth2/';

@Injectable()
export class AuthService {
  private contentHeaders = new HttpHeaders({
    'Accept': 'application/json',
    'Content-Type': 'application/json'
  });

  private jwtHelper: JwtHelperService = new JwtHelperService();

  constructor(private http: HttpClient,
              private currentUser: CurrentUser,
              private store: Store<fromAuth.State>) {
  }

  authenticated(): boolean {
    const jwt = localStorage.getItem('jwt');
    return !!jwt && !this.jwtHelper.isTokenExpired(jwt);
  }

  login(username: string): Observable<User> {
    const body = JSON.stringify({ 'userName': username });
    return this.http.post(LOGIN_URL, body, { headers: this.contentHeaders, responseType: 'text' }).pipe(
      switchMap(jwt => this.loggedIn(jwt))
    );
  }

  loginOAuth(code: string): Observable<User> {
    const params = new HttpParams().set('code', code);
    return this.http.get(OAUTH_URL, {headers: this.contentHeaders, params: params, responseType: 'text'}).pipe(
      switchMap(jwt => this.loggedIn(jwt))
    );
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
