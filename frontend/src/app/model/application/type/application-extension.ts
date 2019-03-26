export abstract class ApplicationExtension {
  constructor(public applicationType?: string,
              public terms?: string) {
  }
}

export interface WorkFinishedDates {
  workFinished?: Date;
  customerWorkFinished?: Date;
  workFinishedReported?: Date;
}

// Check if given object has every property the interface requires
export function isWorkFinishedDates(obj: any): obj is WorkFinishedDates {
  return 'workFinished' in obj
    && 'customerWorkFinished' in obj
    && 'workFinishedReported' in obj;
}

export interface OperationalConditionDates {
  winterTimeOperation?: Date;
  customerWinterTimeOperation?: Date;
  operationalConditionReported?: Date;
}

export function isOperationalConditionDates(obj: any): obj is OperationalConditionDates {
  return 'winterTimeOperation' in obj
  && 'customerWinterTimeOperation' in obj
  && 'operationalConditionReported' in obj;
}

export interface GuaranteeEndTime {
  guaranteeEndTime?: Date;
}

export function isGuaranteeEndTime(obj: any): obj is GuaranteeEndTime {
  return 'guaranteeEndTime' in obj;
}

export interface CustomerStartEndTimes {
  customerStartTime?: Date;
  customerEndTime?: Date;
}

export function isCustomerStartEndTimes(obj: any): obj is CustomerStartEndTimes {
  return 'customerStartTime' in obj
    && 'customerEndTime' in obj;
}
