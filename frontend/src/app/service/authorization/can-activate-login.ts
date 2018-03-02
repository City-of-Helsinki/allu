import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {ConfigService} from '../config/config.service';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class CanActivateLogin implements CanActivate {
  constructor(private configService: ConfigService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.configService.isStagingOrProduction()
      .map(isStagOrProd => {
        if (isStagOrProd) {
          this.router.navigate(['/home']);
        }
        return !isStagOrProd;
      });
  }
}
