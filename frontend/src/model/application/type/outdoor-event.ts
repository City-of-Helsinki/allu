import {ApplicationTypeData} from './application-type-data';

export class OutdoorEvent extends ApplicationTypeData {
  constructor(
    public type: string,
    public description: string,
    public url: string,
    public startDate: Date,
    public endDate: Date,
    public timeExceptions: string,
    public attendees: number,
    public entryFee: number) {
      super();
    }
}
