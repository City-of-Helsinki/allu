import {ApplicationStatusChange} from '../application/application-status-change';
import {DecisionDetails} from './decision-details';

export class DecisionConfirmation {
  constructor(public statusChange: ApplicationStatusChange, public decisionDetails: DecisionDetails) {}
}
