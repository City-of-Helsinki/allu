import {ApplicationTypeData} from './application-type-data';
import {TimeUtil} from '../../../util/time.util';

export class OutdoorEvent extends ApplicationTypeData {
  constructor()
  constructor(nature: string,
              description: string,
              url: string,
              type: string,
              startTime: Date,
              endTime: Date,
              timeExceptions: string,
              attendees: number,
              entryFee: number,
              pricing: string,
              salesActivity: boolean,
              heavyStructure: boolean,
              ecoCompass: boolean,
              foodSales: boolean,
              foodProviders: string,
              marketingProviders: string,
              structureArea: number,
              structureDescription: string,
              structureStartTime: Date,
              structureEndTime: Date)
  constructor(public nature?: string,
              public description?: string,
              public url?: string,
              public type?: string,
              public startTime?: Date,
              public endTime?: Date,
              public timeExceptions?: string,
              public attendees?: number,
              public entryFee?: number,
              public pricing?: string,
              public salesActivity?: boolean,
              public heavyStructure?: boolean,
              public ecoCompass?: boolean,
              public foodSales?: boolean,
              public foodProviders?: string,
              public marketingProviders?: string,
              public structureArea?: number,
              public structureDescription?: string,
              public structureStartTime?: Date,
              public structureEndTime?: Date) {
    super();
  }

  /*
   * Getters and setters for supporting pickadate editing in UI.
   */

  public get uiStartTime(): string {
    return TimeUtil.getUiDateString(this.startTime);
  }

  public set uiStartTime(dateString: string) {
    this.startTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiEndTime(): string {
    return TimeUtil.getUiDateString(this.endTime);
  }

  public set uiEndTime(dateString: string) {
    this.endTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiStructureStartTime(): string {
    return TimeUtil.getUiDateString(this.structureStartTime);
  }

  public set uiStructureStartTime(dateString: string) {
    this.structureStartTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiStructureEndTime(): string {
    return TimeUtil.getUiDateString(this.structureEndTime);
  }

  public set uiStructureEndTime(dateString: string) {
    this.structureEndTime = TimeUtil.getDateFromUi(dateString);
  }
}
