import {Injectable} from '@angular/core';
import {Response, Headers, Http, URLSearchParams} from '@angular/http';
import {JwtHelper} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';

const LOGIN_URL = '/api/auth/login';
const OAUTH_URL = '/api/oauth2/';

@Injectable()
export class AuthService {
  private jwtHelper: JwtHelper = new JwtHelper();
  private contentHeaders = new Headers();

  constructor(private http: Http) {
    this.contentHeaders.append('Accept', 'application/json');
    this.contentHeaders.append('Content-Type', 'application/json');
  }

  authenticated(): boolean {
    let jwt = localStorage.getItem('jwt');
    return !!jwt && !this.jwtHelper.isTokenExpired(jwt);
  }

  login(username: string): Observable<Response> {
    let body = JSON.stringify({ 'userName': username });
    return this.http.post(LOGIN_URL, body, { headers: this.contentHeaders })
      .do(response => localStorage.setItem('jwt', response.text()));
  }

  loginOAuth(code: string): Observable<Response> {
    let searchParams = new URLSearchParams();
    searchParams.append('code', code);
    return this.http.get(OAUTH_URL, {headers: this.contentHeaders, search: searchParams})
      .do(response => localStorage.setItem('jwt', response.text()));
  }

  logout(): void {
    localStorage.removeItem('jwt');
  }
}
