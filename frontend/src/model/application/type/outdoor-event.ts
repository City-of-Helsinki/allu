import {ApplicationTypeData} from './application-type-data';

export class OutdoorEvent extends ApplicationTypeData {
  constructor(
    public nature: string,
    public description: string,
    public url: string,
    public type: string,
    public startTime: Date,
    public endTime: Date,
    public audience: number) {
      super();
    }
}
