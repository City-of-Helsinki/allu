import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/index';
import * as fromApplication from '@feature/application/reducers';
import {map, tap} from 'rxjs/internal/operators';
import {ApplicationType} from '@model/application/type/application-type';

@Injectable()
export class ContractGuard  {
  constructor(private store: Store<fromApplication.State>,
              private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.select(fromApplication.getCurrentApplication).pipe(
      map(app => app.type === ApplicationType.PLACEMENT_CONTRACT),
      tap(canActivate => {
        if (!canActivate) {
          const segments = state.url.split('/');
          const basePath = segments.slice(0, segments.length - 1).join('/');
          this.router.navigate([basePath, 'decision']);
        }
      })
    );
  }
}
