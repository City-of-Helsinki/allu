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
