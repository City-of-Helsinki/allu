import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import {Observable, of} from 'rxjs';
import {AuthService} from './auth.service';
import {ConfigService} from '../config/config.service';
import {REDIRECT_URL} from '../../../util/local-storage';
import {map} from 'rxjs/internal/operators';
import {RoleType} from '@model/user/role-type';
import {CurrentUser} from '@service/user/current-user';
import {Router} from '@angular/router';




@Injectable()
export class AdminGuard  {

  constructor(private authService: AuthService,
              private configService: ConfigService, private currentUser: CurrentUser, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    let isAdmin = false
    this.currentUser.hasRole(['ROLE_ADMIN']).subscribe(
      (hasRole: boolean) => {
        isAdmin = hasRole
      }
    )
    if (this.authService.authenticated()) {
      if (isAdmin) {
        return of(true);
      }
      else {
        this.router.navigateByUrl('/home');
        return of(false);
      }
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
