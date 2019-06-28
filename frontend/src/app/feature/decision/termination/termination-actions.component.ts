import {Component, OnDestroy, OnInit} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Store} from '@ngrx/store';
import {Subject} from 'rxjs/index';
import {takeUntil} from 'rxjs/operators';
import {ApplicationStatus} from '@model/application/application-status';
import {TerminationModalService} from '@feature/decision/termination/termination-modal-service';


@Component({
  selector: 'termination-actions',
  templateUrl: './termination-actions.component.html',
  styleUrls: ['./termination-actions.component.scss']
})
export class TerminationActionsComponent implements OnInit, OnDestroy {

  isWaitingForOrTerminated: boolean;

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private store: Store<fromRoot.State>,
              private terminationModalService: TerminationModalService) {}

  ngOnInit(): void {
    this.store.select(fromApplication.getCurrentApplication).pipe(
      takeUntil(this.destroy)
    ).subscribe(app => {
      // TODO Archived status means that the application either can be terminated, or is already terminated
      // TODO so we can't hide the button for that status yet.
      this.isWaitingForOrTerminated = ApplicationStatus.TERMINATED === app.status;
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  showTerminationModal(): void {
    this.terminationModalService.showTerminationModal();
  }
}
