import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import * as fromApplication from '../reducers';
import {MetadataService} from '../../../service/meta/metadata.service';
import {Observable, of} from 'rxjs/index';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '../../../util/number.util';
import {ApplicationMetaActionType} from '../actions/application-meta-actions';
import * as MetaAction from '../actions/application-meta-actions';
import {Injectable} from '@angular/core';

@Injectable()
export class ApplicationEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private metadataService: MetadataService) {}

  @Effect()
  loadMeta: Observable<Action> = this.actions.pipe(
    ofType<MetaAction.Load>(ApplicationMetaActionType.Load),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.metadataService.loadByApplicationType(application.type).pipe(
      map(meta => new MetaAction.LoadSuccess(meta)),
      catchError(error => of(new MetaAction.LoadFailed(error)))
    ))
  );
}
