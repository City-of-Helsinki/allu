import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import {Observable, of} from 'rxjs';
import {AuthService} from './auth.service';
import {ConfigService} from '../config/config.service';
import {REDIRECT_URL} from '../../../util/local-storage';
import {map} from 'rxjs/internal/operators';

const AUTH_IN_PROGRESS = 'AUTH_IN_PROGRESS';

@Injectable()
export class AuthGuard  {

  constructor(private authService: AuthService,
              private configService: ConfigService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    console.log('LOGINBUG - auth guard canActivate URL', state.url, ' Authenticated', this.authService.authenticated());
    if (this.authService.authenticated()) {
      return of(true);
    } else {
      return this.authenticate(route, state.url);
    }
  }

  private authenticate(route: ActivatedRouteSnapshot, redirectUrl: string): Observable<boolean> {
    const code = route.queryParams['code'];
    console.log('LOGINBUG AuthGuard.authenticate has OAuth code:', !!code, 'RedirectURL', redirectUrl);

    if (code) {
      console.log('LOGINBUG Authguard.authenticate - processing OAuth code');

      return this.authService.loginOAuth(code).pipe(map(response => {
        console.log('LOGINBUG Authguard.authenticate OAuth login completed ok')
        return true;
      }));

    } else {
      console.log('LOGINBUG Authguard.authenticate - no code, redirect to oAuth')
      localStorage.setItem(REDIRECT_URL, redirectUrl);
      this.redirectToOAuth();
      return of(false);
    }
  }

  private redirectToOAuth() {
    console.log('LOGINBUG authguard.redirectToOAuth getting config')
    this.configService.getConfiguration().subscribe(config => {
      console.log('LOGINBUG authguard.redirectToOAuth - redirecting to', config.oauth2AuthorizationEndpointUrl);
      window.location.href = config.oauth2AuthorizationEndpointUrl;
    });
  }
}
