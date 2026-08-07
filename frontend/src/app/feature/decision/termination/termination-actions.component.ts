import {Component} from '@angular/core';
import {TerminationModalService} from '@feature/decision/termination/termination-modal-service';


@Component({
  selector: 'termination-actions',
  templateUrl: './termination-actions.component.html',
  styleUrls: ['./termination-actions.component.scss']
})
export class TerminationActionsComponent {

  isWaitingForOrTerminated: boolean;

  constructor(private terminationModalService: TerminationModalService) {}

  showTerminationModal(): void {
    this.terminationModalService.showTerminationModal();
  }

  removeTerminationInfo(): void {
    this.terminationModalService.confirmDraftRemoval();
  }
}
