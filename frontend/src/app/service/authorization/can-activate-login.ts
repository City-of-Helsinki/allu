import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import {ConfigService} from '../config/config.service';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable()
export class CanActivateLogin  {
  constructor(private configService: ConfigService, private router: Router) {
  }

  canActivate(_route: ActivatedRouteSnapshot, _state: RouterStateSnapshot): Observable<boolean> {
    return this.configService.isStagingOrProduction().pipe(
      map(isStagOrProd => {
        if (isStagOrProd) {
          this.router.navigate(['/home']);
        }
        return !isStagOrProd;
      })
    );
  }
}
