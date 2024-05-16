import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import * as fromProject from '../reducers';
import * as fromAuth from '../../auth/reducers';
import {Observable, of, defer} from 'rxjs';
import {
  Add,
  AddMultiple,
  ApplicationBasketActionType,
  Clear,
  Load,
  LoadSuccess,
  Remove
} from '../actions/application-basket-actions';
import {catchError, combineLatest, filter, map, switchMap, tap} from 'rxjs/operators';
import {ApplicationService} from '@service/application/application.service';
import {LocalStorageUtil} from '@util/local-storage.util';
import {NotificationService} from '@feature/notification/notification.service';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

const BASKET = 'applicationBasket';

@Injectable()
export class ApplicationBasketEffects {
  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private applicationService: ApplicationService,
              private notification: NotificationService) {}

  
  load: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(ApplicationBasketActionType.Load),
    map(() => LocalStorageUtil.getItemArray<number>(BASKET)),
    combineLatest(this.store.select(fromAuth.getLoggedIn)),
    filter(([ids, loggedIn]) => loggedIn),
    switchMap(([ids, loggedIn]) => this.loadApplications(ids))
  ));

  
  add = createEffect(() => this.actions.pipe(
    ofType<Add>(ApplicationBasketActionType.Add),
    map(action => action.payload),
    tap(id => {
      LocalStorageUtil.addItemsToArray(BASKET, [id]);
      this.notification.translateSuccess('applicationBasket.applicationAdded');
    }),
    map(id => new Load([id]))
  ));

  
  addMultiple = createEffect(() => this.actions.pipe(
    ofType<AddMultiple>(ApplicationBasketActionType.AddMultiple),
    map(action => action.payload),
    tap(ids => {
      LocalStorageUtil.addItemsToArray(BASKET, ids);
      this.notification.translateSuccess('applicationBasket.applicationsAdded');
    }),
    map(ids => new Load(ids))
  ));

  
  remove = createEffect(() => this.actions.pipe(
    ofType<Remove>(ApplicationBasketActionType.Remove),
    map(action => action.payload),
    tap(id => {
      LocalStorageUtil.removeItemFromArray(BASKET, id);
      this.notification.translateSuccess('applicationBasket.applicationRemoved');
    })
  ), {dispatch: false});

  
  clear = createEffect(() => this.actions.pipe(
    ofType<Clear>(ApplicationBasketActionType.Clear),
    tap(() => LocalStorageUtil.remove(BASKET))
  ), {dispatch: false});

  
  loadInitial: Observable<Action> = createEffect(() => defer(() => of(LocalStorageUtil.getItemArray<number>(BASKET))).pipe(
    combineLatest(this.store.select(fromAuth.getLoggedIn)),
    filter(([ids, loggedIn]) => loggedIn && ids.length > 0),
    switchMap(([ids, loggedIn]) => this.loadApplications(ids))
  ));

  private loadApplications(ids: number[]): Observable<Action> {
    return this.applicationService.byIds(ids).pipe(
      map(applications => new LoadSuccess(applications)),
      catchError(error => of(new NotifyFailure(error)))
    );
  }
}
