import {Injectable} from '@angular/core';
import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {
  TERMINATION_MODAL_CONFIG,
  TerminationModalComponent
} from '@feature/decision/termination/termination-modal.component';
import {Application} from '@model/application/application';
import {Terminate} from '@feature/decision/actions/termination-actions';
import {filter, map, switchMap, take, withLatestFrom} from 'rxjs/operators';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import * as fromDecision from '@feature/decision/reducers';
import {MatDialog} from '@angular/material';
import {Store} from '@ngrx/store';


@Injectable()
export class TerminationModalService {

  constructor(private store: Store<fromRoot.State>,
              private dialog: MatDialog) {
  }

  private static createConfig(app: Application, termination: TerminationInfo) {
    return {
      ...TERMINATION_MODAL_CONFIG,
      data: {
        applicationType: app.type,
        applicationId: app.id,
        termination: termination
      }
    };
  }

  showTerminationModal(): void {
    this.store.select(fromApplication.getCurrentApplication).pipe(
      take(1),
      withLatestFrom(this.store.select(fromDecision.getTermination)),
      map(([app, termination]) => TerminationModalService.createConfig(app, termination)),
      map(config => this.dialog.open<TerminationModalComponent>(TerminationModalComponent, config)),
      switchMap(modalRef => modalRef.afterClosed()),
      filter(result => !!result)
    ).subscribe(termination => this.store.dispatch(new Terminate(termination)));
  }
}