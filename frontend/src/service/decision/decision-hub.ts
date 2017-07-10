import {Injectable} from '@angular/core';
import '../../rxjs-extensions.ts';
import {DecisionService} from './decision.service';
import {DecisionDetails} from '../../model/decision/decision-details';
import {ApplicationStatus, canBeEdited} from '../../model/application/application-status';

@Injectable()
export class DecisionHub {
  constructor(private decisionService: DecisionService) {
  }

  /**
   * Fetch real decision when application is in decision state or state after it
   * otherwise show preview
   */
  public fetchByStatus = (applicationId: number, status: ApplicationStatus) => canBeEdited(status)
      ? this.fetch(applicationId)
      : this.preview(applicationId);

  /**
   * Asks decision service to fetch decision pdf for given application.
   * Returns observable which contains pdf eventually
   */
  public fetch = (applicationId: number) => this.decisionService.fetch(applicationId);

  /**
   * Asks decision service to fetch preview of decision pdf for given application.
   * Returns observable which contains preview pdf
   */
  public preview = (applicationId: number) => this.decisionService.preview(applicationId);

  /**
   * Sends decision to its recepients based on distribution
   * @param applicationId application id of decision
   * @param decisionDetails containing message and distribution
   */
  public sendDecision = (applicationId: number, decisionDetails: DecisionDetails) =>
    this.decisionService.sendDecision(applicationId, decisionDetails);
}
