import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {ApplicationService} from '../../../service/application/application.service';
import {Observable} from 'rxjs/Observable';
import {Action} from '@ngrx/store';
import {Search, SearchActionType, SearchFailed, SearchSuccess} from '../actions/application-search-actions';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';

@Injectable()
export class SearchEffects {
  constructor(private actions: Actions,
              private applicationService: ApplicationService) {}

  @Effect()
  search: Observable<Action> = this.actions.pipe(
    ofType<Search>(SearchActionType.Search),
    map(action => action.payload),
    filter(term => term && term.length > 2),
    switchMap(searchTerm =>
      this.applicationService.freeTextSearch(searchTerm).pipe(
        map(applications => new SearchSuccess(applications)),
        catchError(error => of(new SearchFailed(error)))
      )
    )
  );
}
