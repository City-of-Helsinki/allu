import {Injectable} from '@angular/core';
import {Headers, Http, URLSearchParams} from '@angular/http';
import {JwtHelper} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {User} from '../../model/user/user';
import {CurrentUser} from '../user/current-user';

const LOGIN_URL = '/api/auth/login';
const OAUTH_URL = '/api/oauth2/';

@Injectable()
export class AuthService {
  private jwtHelper: JwtHelper = new JwtHelper();
  private contentHeaders = new Headers();

  constructor(private http: Http, private currentUser: CurrentUser) {
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
      .switchMap(response => {
        this.storeJwt(response.text());
        return this.currentUser.user;
      });
  }

  loginOAuth(code: string): Observable<User> {
    const searchParams = new URLSearchParams();
    searchParams.append('code', code);
    return this.http.get(OAUTH_URL, {headers: this.contentHeaders, search: searchParams})
      .switchMap(response => {
        this.storeJwt(response.text());
        return this.currentUser.user;
      });
  }

  logout(): void {
    localStorage.removeItem('jwt');
    this.currentUser.clearUser();
  }

  get token(): string {
    return localStorage.getItem('jwt');
  }

  private storeJwt(token: string): void {
    localStorage.setItem('jwt', token);
  }
}
