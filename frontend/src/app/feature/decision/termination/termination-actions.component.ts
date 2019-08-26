import {Component, OnDestroy, OnInit} from '@angular/core';
import {TerminationModalService} from '@feature/decision/termination/termination-modal-service';


@Component({
  selector: 'termination-actions',
  templateUrl: './termination-actions.component.html',
  styleUrls: ['./termination-actions.component.scss']
})
export class TerminationActionsComponent implements OnInit, OnDestroy {

  isWaitingForOrTerminated: boolean;

  constructor(private terminationModalService: TerminationModalService) {}

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
  }

  showTerminationModal(): void {
    this.terminationModalService.showTerminationModal();
  }

  removeTerminationInfo(): void {
    this.terminationModalService.confirmDraftRemoval();
  }
}
