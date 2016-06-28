import {ApplicationTypeData} from './application-type-data';

export class OutdoorEvent extends ApplicationTypeData {
  constructor(
    public nature: string,
    public description: string,
    public url: string,
    public type: string,
    public startTime: Date,
    public endTime: Date,
    public timeExceptions: string,
    public attendees: number,
    public entryFee: number,
    public pricing: string,
    public salesActivity: boolean,
    public ecoCompass: boolean,
    public foodProviders: string,
    public marketingProviders: string,
    public structureArea: number,
    public structureDescription: string,
    public structureStartTime: Date,
    public structureEndTime: Date) {
      super();
    }
}
