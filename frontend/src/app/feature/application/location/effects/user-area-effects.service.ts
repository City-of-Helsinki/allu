import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromAuth from '@feature/auth/reducers';
import {LocationService} from '@service/location.service';
import {Observable} from 'rxjs/internal/Observable';
import {Load, LoadFailed, LoadSuccess, UserAreaActionType} from '@feature/application/location/actions/user-area-actions';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {ArrayUtil} from '@util/array-util';
import {RoleType} from '@model/user/role-type';
import {combineLatest, from} from 'rxjs';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

const userAreasAllowed: RoleType[] = [
  RoleType.ROLE_CREATE_APPLICATION,
  RoleType.ROLE_PROCESS_APPLICATION
];

@Injectable()
export class UserAreaEffects {

  constructor(private actions: Actions, private store: Store<fromRoot.State>, private locationService: LocationService) {
  }

  @Effect()
  load: Observable<Action> = combineLatest([
    this.actions.pipe(ofType<Load>(UserAreaActionType.Load)),
    this.store.pipe(select(fromAuth.getUser), filter(user => !!user))
  ]
  ).pipe(
    filter(([action, user]) => ArrayUtil.anyMatch(userAreasAllowed, user.assignedRoles)),
    switchMap(([action, user]) => this.locationService.getUserAreas().pipe(
      map(featureGroup => new LoadSuccess(featureGroup)),
      catchError(error => from([
        new LoadFailed(),
        new NotifyFailure(error)
      ]))
    ))
  );
}
