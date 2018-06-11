import {Injectable} from '@angular/core';
import {DecisionService} from './decision.service';
import {DecisionDetails} from '../../model/decision/decision-details';

@Injectable()
export class DecisionHub {
  constructor(private decisionService: DecisionService) {
  }

  /**
   * Asks decision service to fetch decision pdf for given application.
   * Returns observable which contains pdf eventually
   */
  public fetch = (applicationId: number) => this.decisionService.fetch(applicationId);

  /**
   * Sends decision to its recepients based on distribution
   * @param applicationId application id of decision
   * @param decisionDetails containing messageToReadable and distribution
   */
  public sendDecision = (applicationId: number, decisionDetails: DecisionDetails) =>
    this.decisionService.sendDecision(applicationId, decisionDetails)
}
