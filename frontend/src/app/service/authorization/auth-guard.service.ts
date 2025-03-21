import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import {Observable, of} from 'rxjs';
import {AuthService} from './auth.service';
import {ConfigService} from '../config/config.service';
import {REDIRECT_URL} from '../../../util/local-storage';
import {map, debounceTime, take} from 'rxjs/internal/operators';

const REDIRECT_IN_PROGRESS = 'REDIRECT_IN_PROGRESS';

@Injectable()
export class AuthGuard  {

  constructor(private authService: AuthService,
              private configService: ConfigService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    console.log('LOGINBUG - auth guard canActivate URL', state.url, ' Authenticated', this.authService.authenticated());
    if (this.authService.authenticated()) {
      return of(true);
    } 

    const redirectInProgress = sessionStorage.getItem(REDIRECT_IN_PROGRESS);
    const currentTime = new Date().getTime();

    if (redirectInProgress && (currentTime - parseInt(redirectInProgress, 10)) < 2000) {
      console.log('LOGINBUG - another tab is already authing');
      return of(false);
    }

    return this.authenticate(route, state.url);
  }

  private authenticate(route: ActivatedRouteSnapshot, redirectUrl: string): Observable<boolean> {
    const code = route.queryParams['code'];
    console.log('LOGINBUG AuthGuard.authenticate has OAuth code:', !!code, 'RedirectURL', redirectUrl);

    if (code) {
      console.log('LOGINBUG Authguard.authenticate - processing OAuth code');

      return this.authService.loginOAuth(code).pipe(
        take(1),
        map(response => {
        console.log('LOGINBUG Authguard.authenticate OAuth login completed ok', !!response);
        sessionStorage.removeItem(REDIRECT_IN_PROGRESS);
        return true;
      }));

    } else {
      console.log('LOGINBUG Authguard.authenticate - no code, redirect to oAuth')
      localStorage.setItem(REDIRECT_URL, redirectUrl);
      sessionStorage.setItem(REDIRECT_IN_PROGRESS, new Date().getTime().toString());
      this.redirectToOAuth();
      return of(false);
    }
  }

  private redirectToOAuth() {
    console.log('LOGINBUG authguard.redirectToOAuth getting config')
    this.configService.getConfiguration().pipe(
      take(1),
      debounceTime(100)
    ).subscribe(config => {
      console.log('LOGINBUG authguard.redirectToOAuth - redirecting to', config.oauth2AuthorizationEndpointUrl);
      window.location.href = config.oauth2AuthorizationEndpointUrl;
    });
  }
}
