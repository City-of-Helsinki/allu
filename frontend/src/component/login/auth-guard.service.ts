import {Injectable} from '@angular/core';
import {Router, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate} from '@angular/router';
import {JwtHelper} from 'angular2-jwt/angular2-jwt';

@Injectable()
export class AuthGuard implements CanActivate {
  private jwtHelper: JwtHelper;

  constructor(private router: Router) {
    this.jwtHelper = new JwtHelper;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    let jwt = localStorage.getItem('jwt');
    if (!jwt || this.jwtHelper.isTokenExpired(jwt)) {
      // todo: redirect to Login, may be there a better way?
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }
}
