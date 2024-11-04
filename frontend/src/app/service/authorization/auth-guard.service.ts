import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import {Observable, of} from 'rxjs';
import {AuthService} from './auth.service';
import {ConfigService} from '../config/config.service';
import {REDIRECT_URL} from '../../../util/local-storage';
import {map} from 'rxjs/internal/operators';

@Injectable()
export class AuthGuard  {

  constructor(private authService: AuthService,
              private configService: ConfigService) {
  }

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<Observable<boolean>> {
    const authenticated = await this.authService.authenticated();
    if (authenticated) {
      return of(true);
    } else {
      return this.authenticate(route, state.url);
    }
  }

  private authenticate(route: ActivatedRouteSnapshot, redirectUrl: string): Observable<boolean> {
    const code = route.queryParams['code'];
    if (code) {
      return this.authService.loginOAuth(code).pipe(map(response => true));
    } else {
      localStorage.setItem(REDIRECT_URL, redirectUrl);
      this.redirectToOAuth();
      return of(false);
    }
  }

  private redirectToOAuth() {
    this.configService.getConfiguration().subscribe(config => {
      window.location.href = config.oauth2AuthorizationEndpointUrl;
    });
  }
}
