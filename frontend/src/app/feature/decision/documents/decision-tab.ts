import {ApplicationStatus} from '@model/application/application-status';

export enum DecisionTab {
  DECISION = 'DECISION',
  CONTRACT = 'CONTRACT',
  OPERATIONAL_CONDITION = 'OPERATIONAL_CONDITION',
  WORK_FINISHED = 'WORK_FINISHED',
  TERMINATION = 'TERMINATION'
}

export const tabToStatus: {[key: string]: ApplicationStatus} = {
  DECISION: ApplicationStatus.DECISION,
  CONTRACT: undefined,
  OPERATIONAL_CONDITION:  ApplicationStatus.OPERATIONAL_CONDITION,
  WORK_FINISHED:  ApplicationStatus.FINISHED,
  TERMINATION: ApplicationStatus.TERMINATED
};
