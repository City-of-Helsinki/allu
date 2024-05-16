import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {ApplicationService} from '@service/application/application.service';
import {Observable, of} from 'rxjs';
import {Action} from '@ngrx/store';
import * as project from '../actions/project-search-actions';
import {ProjectSearchActionType} from '../actions/project-search-actions';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {CustomerService} from '@service/customer/customer.service';
import {ProjectService} from '@service/project/project.service';

@Injectable()
export class SearchEffects {
  constructor(private actions: Actions,
              private applicationService: ApplicationService,
              private customerService: CustomerService,
              private projectService: ProjectService) {}

  
  projectSearch: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<project.Search>(ProjectSearchActionType.Search),
    map(action => action.payload),
    switchMap(params => this.projectService.pagedSearch(params.query, params.sort, params.pageRequest).pipe(
      map(projects => new project.SearchSuccess(projects)),
      catchError(error => of(new project.SearchFailed(error)))
    ))
  ));
}
