import {Actions, createEffect} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {defer, Observable, of} from 'rxjs';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import * as fromAuth from '../../auth/reducers';
import {CodeSetService} from '../../../service/codeset/codeset.service';
import {LoadFailed, LoadSuccess} from '../actions/code-set-actions';
import {CodeSet, CodeSetTypeMap} from '../../../model/codeset/codeset';

@Injectable()
export class CodeSetEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private codeSetService: CodeSetService) {}

  
  init: Observable<Action> = createEffect(() => defer(() => this.store.select(fromAuth.getLoggedIn).pipe(
    filter(loggedIn => loggedIn),
    switchMap(() => this.codeSetService.getCountries().pipe(
      map(codeSets => this.toCodeSetMap(codeSets)),
      map(codeSets => new LoadSuccess(codeSets)),
      catchError(error => of(new LoadFailed(error))))
    ))
  ));

  private toCodeSetMap(codeSets: CodeSet[]): CodeSetTypeMap {
    return codeSets.reduce((prev: CodeSetTypeMap, cur) => {
      const typeNode = prev[cur.type] || {};
      typeNode[cur.code] = cur;
      prev[cur.type] = typeNode;
      return prev;
    }, {});
  }
}
