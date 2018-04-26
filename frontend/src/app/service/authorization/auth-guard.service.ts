import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {AuthService} from './auth.service';
import {ConfigService} from '../config/config.service';
import {REDIRECT_URL} from '../../../util/local-storage';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService,
              private configService: ConfigService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    if (this.authService.authenticated()) {
      return Observable.of(true);
    } else {
      return this.authenticate(route, state.url);
    }
  }

  private authenticate(route: ActivatedRouteSnapshot, redirectUrl: string): Observable<boolean> {
    const code = route.queryParams['code'];
    if (code) {
      return this.authService.loginOAuth(code).map(response => true);
    } else {
      localStorage.setItem(REDIRECT_URL, redirectUrl);
      this.redirectToOAuth();
      return Observable.of(false);
    }
  }

  private redirectToOAuth() {
    this.configService.getConfiguration().subscribe(config => {
      window.location.href = config.oauth2AuthorizationEndpointUrl;
    });
  }
}
