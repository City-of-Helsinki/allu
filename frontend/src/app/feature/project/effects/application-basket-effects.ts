import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import * as fromProject from '../reducers';
import * as fromAuth from '../../auth/reducers';
import {Observable, of, defer} from 'rxjs';
import {
  Add, AddMultiple,
  ApplicationBasketActionType, Clear,
  Load,
  LoadFailed,
  LoadSuccess,
  Remove
} from '../actions/application-basket-actions';
import {catchError, combineLatest, filter, map, switchMap, tap} from 'rxjs/operators';
import {ApplicationService} from '../../../service/application/application.service';
import {LocalStorageUtil} from '../../../util/local-storage.util';
import {NotificationService} from '../../../service/notification/notification.service';

const BASKET = 'applicationBasket';

@Injectable()
export class ApplicationBasketEffects {
  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private applicationService: ApplicationService,
              private notification: NotificationService) {}

  @Effect()
  load: Observable<Action> = this.actions.pipe(
    ofType<Load>(ApplicationBasketActionType.Load),
    map(() => LocalStorageUtil.getItemArray<number>(BASKET)),
    combineLatest(this.store.select(fromAuth.getLoggedIn)),
    filter(([ids, loggedIn]) => loggedIn),
    switchMap(([ids, loggedIn]) => this.loadApplications(ids))
  );

  @Effect()
  add = this.actions.pipe(
    ofType<Add>(ApplicationBasketActionType.Add),
    map(action => action.payload),
    tap(id => {
      LocalStorageUtil.addItemsToArray(BASKET, [id]);
      this.notification.translateSuccess('applicationBasket.applicationAdded');
    }),
    map(id => new Load([id]))
  );

  @Effect()
  addMultiple = this.actions.pipe(
    ofType<AddMultiple>(ApplicationBasketActionType.AddMultiple),
    map(action => action.payload),
    tap(ids => {
      LocalStorageUtil.addItemsToArray(BASKET, ids);
      this.notification.translateSuccess('applicationBasket.applicationsAdded');
    }),
    map(ids => new Load(ids))
  );

  @Effect({dispatch: false})
  remove = this.actions.pipe(
    ofType<Remove>(ApplicationBasketActionType.Remove),
    map(action => action.payload),
    tap(id => {
      LocalStorageUtil.removeItemFromArray(BASKET, id);
      this.notification.translateSuccess('applicationBasket.applicationRemoved');
    })
  );

  @Effect({dispatch: false})
  clear = this.actions.pipe(
    ofType<Clear>(ApplicationBasketActionType.Clear),
    tap(() => LocalStorageUtil.remove(BASKET))
  );

  @Effect()
  loadInitial: Observable<Action> = defer(() => of(LocalStorageUtil.getItemArray<number>(BASKET))).pipe(
    combineLatest(this.store.select(fromAuth.getLoggedIn)),
    filter(([ids, loggedIn]) => loggedIn),
    switchMap(([ids, loggedIn]) => this.loadApplications(ids))
  );

  private loadApplications(ids: number[]): Observable<Action> {
    return this.applicationService.byIds(ids).pipe(
      map(applications => new LoadSuccess(applications)),
      catchError(error => of(new LoadFailed(error)))
    );
  }
}
